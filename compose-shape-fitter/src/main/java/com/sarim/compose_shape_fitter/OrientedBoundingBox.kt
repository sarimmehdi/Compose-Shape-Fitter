package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class ObbShape(val color: Color, val strokeWidth: Float, val allSidesEqual: Boolean) : DrawableShape {
    // Assuming you have this from a previous context or define it here
    data class OrientedBoundingBox(
        val center: Offset,
        val width: Float,      // Full width of the OBB
        val height: Float,     // Full height of the OBB
        val angleRad: Float,   // Angle of the 'width' axis relative to the positive x-axis
        val corner1: Offset,   // Calculated corners
        val corner2: Offset,
        val corner3: Offset,
        val corner4: Offset
    ) : ApproximatedShape

    private fun createOBBFromEllipse(ellipse: SkewedEllipseShape.RotatedEllipse, allSidesEqual: Boolean): OrientedBoundingBox {
        val centerX = ellipse.center.x
        val centerY = ellipse.center.y
        // <<< NO CHANGE UP TO HERE >>>

        // If your RotatedEllipse.radiusX is always the semi-axis length whose orientation
        // is given by angleRad, and RotatedEllipse.radiusY is the other semi-axis length.
        // The full width of the OBB will be 2 * radiusX
        // The full height of the OBB will be 2 * radiusY
        val ellipseBasedWidth = ellipse.radiusX * 2f  // <<< CHANGED VARIABLE NAME FOR CLARITY >>>
        val ellipseBasedHeight = ellipse.radiusY * 2f // <<< CHANGED VARIABLE NAME FOR CLARITY >>>
        val angleRad = ellipse.angleRad

        // +++ START OF INSERTED/MODIFIED BLOCK +++
        val finalWidth: Float
        val finalHeight: Float

        if (allSidesEqual) {
            val sideLength = max(ellipseBasedWidth, ellipseBasedHeight)
            finalWidth = sideLength
            finalHeight = sideLength
        } else {
            finalWidth = ellipseBasedWidth
            finalHeight = ellipseBasedHeight
        }
        // +++ END OF INSERTED/MODIFIED BLOCK +++

        // Calculate the four corner points of this OBB.
        // The OBB is centered at (centerX, centerY).
        // Its half-width is ellipse.radiusX and half-height is ellipse.radiusY.
        // These half-dimensions are along the rotated axes.

        val cosA = cos(angleRad)
        val sinA = sin(angleRad)

        // Half dimensions
        // <<< MODIFIED BLOCK FOR HALF DIMENSIONS >>>
        val hw = finalWidth / 2f  // <<< USES finalWidth >>>
        val hh = finalHeight / 2f // <<< USES finalHeight >>>
        // <<< END OF MODIFIED BLOCK FOR HALF DIMENSIONS >>>


        // Corner offsets from the center IN THE ROTATED FRAME
        // (x', y') where x' is along the ellipse.radiusX axis, y' is along ellipse.radiusY axis
        val localCorners = listOf(
            Offset(-hw, -hh), // Local Top-Left (or Bottom-Left depending on y-axis direction)
            Offset(hw, -hh),  // Local Top-Right
            Offset(hw, hh),   // Local Bottom-Right
            Offset(-hw, hh)   // Local Bottom-Left
        ) // <<< NO CHANGE TO THIS localCorners LIST STRUCTURE, BUT USES NEW hw, hh >>>

        // Transform local corners to world coordinates
        val worldCorners = localCorners.map { localCorner ->
            val worldX = centerX + localCorner.x * cosA - localCorner.y * sinA
            val worldY = centerY + localCorner.x * sinA + localCorner.y * cosA
            Offset(worldX, worldY)
        } // <<< NO CHANGE TO THIS TRANSFORMATION LOGIC >>>

        return OrientedBoundingBox(
            center = ellipse.center,
            width = finalWidth,    // <<< USES finalWidth >>>
            height = finalHeight,  // <<< USES finalHeight >>>
            angleRad = angleRad,
            corner1 = worldCorners[0], // Top-Left-ish after rotation
            corner2 = worldCorners[1], // Top-Right-ish
            corner3 = worldCorners[2], // Bottom-Right-ish
            corner4 = worldCorners[3]  // Bottom-Left-ish
        ) // <<< NO OTHER CHANGES TO THE RETURN STATEMENT STRUCTURE >>>
    }


    /**
     * Function that uses ellipseParamsArray (from JNI) to create an OrientedBoundingBox
     * for the described ellipse.
     */
    private fun findSmallestEnclosingObb(points: List<Offset>, allSidesEqual: Boolean): OrientedBoundingBox? {
        if (points.isEmpty()) {
            println("Point list is empty, cannot fit ellipse to derive OBB.")
            return null
        }

        val pointsX = FloatArray(points.size) { points[it].x }
        val pointsY = FloatArray(points.size) { points[it].y }

        try {
            // This is your existing JNI call
            val ellipseParamsArray: FloatArray? = EllipseFitterJNI.fitEllipseNative(pointsX, pointsY)

            if (ellipseParamsArray != null && ellipseParamsArray.size == 5) {
                val centerX = ellipseParamsArray[0]
                val centerY = ellipseParamsArray[1]
                // Assuming from JNI:
                // ellipseParamsArray[2] is rl (semi-major axis length if convention holds)
                // ellipseParamsArray[3] is rs (semi-minor axis length if convention holds)
                // ellipseParamsArray[4] is phi (angle of rl)
                // For the OBB derived from *this* ellipse, these radii directly give half-dimensions.

                // Let's assume the JNI output means:
                // Param2 is the semi-axis length along the direction of 'angleRad'
                // Param3 is the semi-axis length perpendicular to 'angleRad'
                // In your current RotatedEllipse, you have:
                // radiusY = ellipseParamsArray[2]
                // radiusX = ellipseParamsArray[3]
                // angleRad = ellipseParamsArray[4]
                // This means your 'radiusX' (from param3) is intended to be aligned with 'angleRad'
                // and 'radiusY' (from param2) is perpendicular. This seems a bit swapped from typical RL/RS
                // but let's adhere to your `RotatedEllipse` structure.


                val ellipseRadiusX = ellipseParamsArray[3] // Your code maps param3 to radiusX
                val ellipseRadiusY = ellipseParamsArray[2] // Your code maps param2 to radiusY
                val ellipseAngleRad = ellipseParamsArray[4]

                if (ellipseRadiusX <= 0f || ellipseRadiusY <= 0f) {
                    println("Native method returned invalid ellipse radii for OBB: rX=$ellipseRadiusX, rY=$ellipseRadiusY")
                    return null
                }

                val fittedEllipse = SkewedEllipseShape.RotatedEllipse(
                    center = Offset(centerX, centerY),
                    radiusX = ellipseRadiusX, // Semi-axis whose orientation is angleRad
                    radiusY = ellipseRadiusY, // Semi-axis perpendicular to angleRad
                    angleRad = ellipseAngleRad
                )

                return createOBBFromEllipse(fittedEllipse, allSidesEqual)

            } else {
                println("Native method 'fitEllipseNative' returned null or an array of unexpected size for OBB: ${ellipseParamsArray?.size ?: "null"}. Expected 5.")
                return null
            }
        } catch (e: UnsatisfiedLinkError) {
            println("JNI UnsatisfiedLinkError in findOBBForFittedEllipse: ${e.message}")
            e.printStackTrace()
            return null
        } catch (e: Exception) {
            println("Exception during JNI ellipse fitting for OBB: ${e.message}")
            e.printStackTrace()
            return null
        }
    }

    override fun draw(drawScope: DrawScope, points: List<Offset>) {
        findSmallestEnclosingObb(points, allSidesEqual)?.let { obb ->
            drawScope.rotate(
                degrees = obb.angleRad * (180f / PI.toFloat()), // Convert radians to degrees
                pivot = obb.center
            ) {
                drawRect(
                    color = color,
                    topLeft = Offset(
                        obb.center.x - obb.width / 2f,
                        obb.center.y - obb.height / 2f
                    ),
                    size = Size(obb.width, obb.height),
                    style = Stroke(width = strokeWidth)
                )
            }
        }
    }

    override fun getApproximatedShape(points: List<Offset>) = findSmallestEnclosingObb(points, allSidesEqual)
}