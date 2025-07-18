package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI

internal object EllipseFitterJNI {
    init {
        try {
            System.loadLibrary("composeshapefittersampleapp_native")
        } catch (e: UnsatisfiedLinkError) {
            println("Failed to load native library 'native-lib': ${e.message}")
        }
    }

    external fun fitEllipseNative(pointsX: FloatArray, pointsY: FloatArray): FloatArray?
}

class SkewedEllipseShape(val color: Color, val strokeWidth: Float) : DrawableShape {

    data class RotatedEllipse(
        val center: Offset,
        val radiusX: Float,
        val radiusY: Float,
        val angleRad: Float
    ) : ApproximatedShape

    private fun findSmallestEnclosingSkewedEllipse(points: List<Offset>): RotatedEllipse? {
        if (points.isEmpty()) {
            println("Point list is empty, cannot fit ellipse.")
            return null
        }

        val pointsX = FloatArray(points.size) { points[it].x }
        val pointsY = FloatArray(points.size) { points[it].y }

        try {
            val ellipseParamsArray: FloatArray? = EllipseFitterJNI.fitEllipseNative(pointsX, pointsY)

            if (ellipseParamsArray != null && ellipseParamsArray.size == 5) {
                val centerX = ellipseParamsArray[0]
                val centerY = ellipseParamsArray[1]
                val radiusY = ellipseParamsArray[2]
                val radiusX = ellipseParamsArray[3]
                val angleRad = ellipseParamsArray[4]

                if (radiusX <= 0f || radiusY <= 0f) {
                    println("Native method returned invalid ellipse radii: rX=$radiusX, rY=$radiusY")
                    return null
                }

                return RotatedEllipse(
                    center = Offset(centerX, centerY),
                    radiusX = radiusX,
                    radiusY = radiusY,
                    angleRad = angleRad
                )
            } else {
                println("Native method 'fitEllipseNative' returned null or an array of unexpected size: ${ellipseParamsArray?.size ?: "null"}. Expected 5.")
                return null
            }
        } catch (e: UnsatisfiedLinkError) {
            println("JNI UnsatisfiedLinkError in findEllipseUsingJNI: ${e.message}")
            e.printStackTrace()
            return null
        } catch (e: Exception) {
            println("Exception during JNI ellipse fitting: ${e.message}")
            e.printStackTrace()
            return null
        }
    }

    override fun draw(drawScope: DrawScope, points: List<Offset>) {
        findSmallestEnclosingSkewedEllipse(points)?.let { rotatedEllipse ->
            drawScope.rotate(
                degrees = rotatedEllipse.angleRad * (180f / PI.toFloat()),
                pivot = rotatedEllipse.center
            ) {
                drawOval(
                    color = color,
                    topLeft = Offset(
                        rotatedEllipse.center.x - rotatedEllipse.radiusX,
                        rotatedEllipse.center.y - rotatedEllipse.radiusY
                    ),
                    size = Size(
                        rotatedEllipse.radiusX * 2,
                        rotatedEllipse.radiusY * 2
                    ),
                    style = Stroke(width = strokeWidth)
                )
            }
        }
    }

    override fun getApproximatedShape(points: List<Offset>) = findSmallestEnclosingSkewedEllipse(points)
}
