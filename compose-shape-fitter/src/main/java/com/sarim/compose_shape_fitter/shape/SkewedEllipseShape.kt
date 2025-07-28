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

internal object EllipseFitterJNI {
    init {
        try {
            System.loadLibrary("composeshapefittersampleapp_native")
        } catch (e: UnsatisfiedLinkError) {
            println("Failed to load native library 'native-lib': ${e.message}")
        }
    }

    external fun fitEllipseNative(
        pointsX: FloatArray,
        pointsY: FloatArray,
    ): FloatArray?
}

class SkewedEllipseShape(
    val color: Color,
    val strokeWidth: Float,
) : DrawableShape {
    @Parcelize
    data class RotatedEllipse(
        val center: @WriteWith<OffsetParceler> Offset,
        val radiusX: Float,
        val radiusY: Float,
        val angleRad: Float,
    ) : ApproximatedShape

    private fun findSmallestEnclosingSkewedEllipse(points: List<Offset>): RotatedEllipse? {
        if (points.isEmpty()) {
            println("Point list is empty, cannot fit ellipse.")
            return null
        }

        var resultEllipse: RotatedEllipse? = null
        val pointsX = FloatArray(points.size) { points[it].x }
        val pointsY = FloatArray(points.size) { points[it].y }

        try {
            val ellipseParamsArray: FloatArray? = EllipseFitterJNI.fitEllipseNative(pointsX, pointsY)

            if (ellipseParamsArray != null && ellipseParamsArray.size == MAX_ELLIPSE_PARAMS) {
                val centerX = ellipseParamsArray[CENTER_X_IDX]
                val centerY = ellipseParamsArray[CENTER_Y_IDX]
                val radiusY = ellipseParamsArray[RAD_Y_IDX]
                val radiusX = ellipseParamsArray[RAD_X_IDX]
                val angleRad = ellipseParamsArray[ANGLE_RAD_IDX]

                if (radiusX > 0f && radiusY > 0f) {
                    resultEllipse =
                        RotatedEllipse(
                            center = Offset(centerX, centerY),
                            radiusX = radiusX,
                            radiusY = radiusY,
                            angleRad = angleRad,
                        )
                } else {
                    println("Native method returned invalid ellipse radii: rX=$radiusX, rY=$radiusY")
                }
            } else {
                println(
                    "Native method 'fitEllipseNative' returned null or an array of unexpected size: " +
                        "${ellipseParamsArray?.size ?: "null"}. Expected $MAX_ELLIPSE_PARAMS.",
                )
            }
        } catch (e: UnsatisfiedLinkError) {
            println("JNI UnsatisfiedLinkError in findEllipseUsingJNI: ${e.message}")
        } catch (
            @Suppress("TooGenericExceptionCaught") e: Exception,
        ) {
            println("Exception during JNI ellipse fitting: ${e.message}")
        }

        return resultEllipse
    }

    override fun draw(
        drawScope: DrawScope,
        points: List<Offset>,
    ) {
        findSmallestEnclosingSkewedEllipse(points)?.let { rotatedEllipse ->
            drawScope.rotate(
                degrees = rotatedEllipse.angleRad * (180f / PI.toFloat()),
                pivot = rotatedEllipse.center,
            ) {
                drawOval(
                    color = color,
                    topLeft =
                        Offset(
                            rotatedEllipse.center.x - rotatedEllipse.radiusX,
                            rotatedEllipse.center.y - rotatedEllipse.radiusY,
                        ),
                    size =
                        Size(
                            rotatedEllipse.radiusX * 2,
                            rotatedEllipse.radiusY * 2,
                        ),
                    style = Stroke(width = strokeWidth),
                )
            }
        }
    }

    override fun getApproximatedShape(points: List<Offset>) = findSmallestEnclosingSkewedEllipse(points)

    companion object {
        private const val CENTER_X_IDX = 0
        private const val CENTER_Y_IDX = 1
        private const val RAD_X_IDX = 3
        private const val RAD_Y_IDX = 2
        private const val ANGLE_RAD_IDX = 4
        private const val MAX_ELLIPSE_PARAMS = 5
    }
}
