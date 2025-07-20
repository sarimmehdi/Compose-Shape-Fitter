package com.sarim.compose_shape_fitter.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.sarim.compose_shape_fitter.utils.OffsetParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class ObbShape(
    val color: Color,
    val strokeWidth: Float,
    val allSidesEqual: Boolean,
) : DrawableShape {
    @Parcelize
    data class OrientedBoundingBox(
        val center: @WriteWith<OffsetParceler> Offset,
        val width: Float,
        val height: Float,
        val angleRad: Float,
        val corner1: @WriteWith<OffsetParceler> Offset,
        val corner2: @WriteWith<OffsetParceler> Offset,
        val corner3: @WriteWith<OffsetParceler> Offset,
        val corner4: @WriteWith<OffsetParceler> Offset,
    ) : ApproximatedShape

    private fun createOBBFromEllipse(
        ellipse: SkewedEllipseShape.RotatedEllipse,
        allSidesEqual: Boolean,
    ): OrientedBoundingBox {
        val centerX = ellipse.center.x
        val centerY = ellipse.center.y
        val ellipseBasedWidth = ellipse.radiusX * 2f
        val ellipseBasedHeight = ellipse.radiusY * 2f
        val angleRad = ellipse.angleRad

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

        val cosA = cos(angleRad)
        val sinA = sin(angleRad)

        val hw = finalWidth / 2f
        val hh = finalHeight / 2f

        val localCorners =
            listOf(
                Offset(-hw, -hh),
                Offset(hw, -hh),
                Offset(hw, hh),
                Offset(-hw, hh),
            )

        val worldCorners =
            localCorners.map { localCorner ->
                val worldX = centerX + localCorner.x * cosA - localCorner.y * sinA
                val worldY = centerY + localCorner.x * sinA + localCorner.y * cosA
                Offset(worldX, worldY)
            }

        return OrientedBoundingBox(
            center = ellipse.center,
            width = finalWidth,
            height = finalHeight,
            angleRad = angleRad,
            corner1 = worldCorners[0],
            corner2 = worldCorners[1],
            corner3 = worldCorners[2],
            corner4 = worldCorners[3],
        )
    }

    private fun findSmallestEnclosingObb(
        points: List<Offset>,
        allSidesEqual: Boolean,
    ): OrientedBoundingBox? {
        if (points.isEmpty()) {
            println("Point list is empty, cannot fit ellipse to derive OBB.")
            return null
        }

        var obb: OrientedBoundingBox? = null // Initialize result to null
        val pointsX = FloatArray(points.size) { points[it].x }
        val pointsY = FloatArray(points.size) { points[it].y }

        try {
            val ellipseParamsArray: FloatArray? = EllipseFitterJNI.fitEllipseNative(pointsX, pointsY)

            if (ellipseParamsArray != null && ellipseParamsArray.size == MAX_ELLIPSE_PARAMS) {
                val centerX = ellipseParamsArray[CENTER_X_IDX]
                val centerY = ellipseParamsArray[CENTER_Y_IDX]

                val ellipseRadiusX = ellipseParamsArray[ELLIPSE_RAD_X_IDX]
                val ellipseRadiusY = ellipseParamsArray[ELLIPSE_RAD_Y_IDX]
                val ellipseAngleRad = ellipseParamsArray[ELLIPSE_ANGLE_RAD_IDX]

                if (ellipseRadiusX > 0f && ellipseRadiusY > 0f) {
                    val fittedEllipse =
                        SkewedEllipseShape.RotatedEllipse(
                            center = Offset(centerX, centerY),
                            radiusX = ellipseRadiusX,
                            radiusY = ellipseRadiusY,
                            angleRad = ellipseAngleRad,
                        )
                    obb = createOBBFromEllipse(fittedEllipse, allSidesEqual) // Assign to obb
                } else {
                    println(
                        "Native method returned invalid ellipse radii for OBB: " +
                                "rX=$ellipseRadiusX, rY=$ellipseRadiusY"
                    )
                }
            } else {
                println(
                    "Native method 'fitEllipseNative' returned null or an array of unexpected size " +
                            "for OBB: ${ellipseParamsArray?.size ?: "null"}. Expected $MAX_ELLIPSE_PARAMS."
                )
            }
        } catch (e: UnsatisfiedLinkError) {
            println("JNI UnsatisfiedLinkError in findOBBForFittedEllipse: ${e.message}")
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            println("Exception during JNI ellipse fitting for OBB: ${e.message}")
        }

        return obb
    }

    override fun draw(
        drawScope: DrawScope,
        points: List<Offset>,
    ) {
        findSmallestEnclosingObb(points, allSidesEqual)?.let { obb ->
            drawScope.rotate(
                degrees = obb.angleRad * (180f / PI.toFloat()),
                pivot = obb.center,
            ) {
                drawRect(
                    color = color,
                    topLeft =
                        Offset(
                            obb.center.x - obb.width / 2f,
                            obb.center.y - obb.height / 2f,
                        ),
                    size = Size(obb.width, obb.height),
                    style = Stroke(width = strokeWidth),
                )
            }
        }
    }

    override fun getApproximatedShape(points: List<Offset>) = findSmallestEnclosingObb(points, allSidesEqual)

    companion object {
        private const val CENTER_X_IDX = 0
        private const val CENTER_Y_IDX = 1
        private const val ELLIPSE_RAD_X_IDX = 3
        private const val ELLIPSE_RAD_Y_IDX = 2
        private const val ELLIPSE_ANGLE_RAD_IDX = 4
        private const val MAX_ELLIPSE_PARAMS = 5
    }
}
