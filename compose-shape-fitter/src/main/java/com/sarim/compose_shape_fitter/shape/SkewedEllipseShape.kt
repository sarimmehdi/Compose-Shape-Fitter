package com.sarim.compose_shape_fitter.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.sarim.compose_shape_fitter.BuildConfig
import com.sarim.compose_shape_fitter.shape.DrawableShape.Companion.DEFAULT_IN_PREVIEW_MODE
import com.sarim.compose_shape_fitter.shape.DrawableShape.Companion.DEFAULT_LOG_REGARDLESS
import com.sarim.compose_shape_fitter.utils.LogType
import com.sarim.compose_shape_fitter.utils.OffsetParceler
import com.sarim.compose_shape_fitter.utils.log
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import kotlin.math.PI
import kotlin.math.max as mathMax
import kotlin.math.min as mathMin

internal object EllipseFitterJNI {
    init {
        try {
            System.loadLibrary("composeshapefitters_native")
        } catch (e: UnsatisfiedLinkError) {
            throw IllegalStateException("Failed to initialize EllipseFitterJNI due to missing native library", e)
        }
    }

    external fun fitEllipseNative(
        pointsX: FloatArray,
        pointsY: FloatArray,
        shouldLog: Boolean,
    ): FloatArray?
}

class SkewedEllipseShape(
    val color: Color,
    val strokeWidth: Float,
    override var logRegardless: Boolean = DEFAULT_LOG_REGARDLESS,
    override var inPreviewMode: Boolean = DEFAULT_IN_PREVIEW_MODE,
) : DrawableShape {
    @Parcelize
    data class RotatedEllipse(
        val center: @WriteWith<OffsetParceler> Offset,
        val radiusX: Float,
        val radiusY: Float,
        val angleRad: Float,
    ) : ApproximatedShape

    private fun createTiltedEllipse(points: List<Offset>): RotatedEllipse {
        var minX = points[0].x
        var maxX = points[0].x
        var minY = points[0].y
        var maxY = points[0].y

        for (i in 1 until points.size) {
            minX = mathMin(minX, points[i].x)
            maxX = mathMax(maxX, points[i].x)
            minY = mathMin(minY, points[i].y)
            maxY = mathMax(maxY, points[i].y)
        }

        val estimatedWidth = mathMax(2f, maxX - minX)
        val estimatedHeight = mathMax(2f, maxY - minY)

        val previewCenterX = minX + estimatedWidth / 2f
        val previewCenterY = minY + estimatedHeight / 2f
        val previewCenter = Offset(previewCenterX, previewCenterY)

        val previewRadiusX = estimatedWidth / 2f
        val previewRadiusY = estimatedHeight / 2f

        return RotatedEllipse(
            center = previewCenter,
            radiusX = previewRadiusX,
            radiusY = previewRadiusY,
            angleRad = PI_SIXTH,
        )
    }

    private fun findSmallestEnclosingSkewedEllipse(points: List<Offset>): RotatedEllipse? {
        val resultEllipse: RotatedEllipse?

        if (points.isEmpty()) {
            log(
                tag = SkewedEllipseShape::class.java.simpleName,
                messageBuilder = { "Point list is empty, cannot fit ellipse." },
                logType = LogType.WARN,
                logRegardless = logRegardless,
            )
            resultEllipse = null
        } else if (inPreviewMode) {
            resultEllipse = createTiltedEllipse(points)
        } else {
            resultEllipse = fitEllipseWithJNI(points)
        }

        return resultEllipse
    }

    @Suppress("LongMethod")
    private fun fitEllipseWithJNI(points: List<Offset>): RotatedEllipse? {
        val pointsX = FloatArray(points.size) { points[it].x }
        val pointsY = FloatArray(points.size) { points[it].y }
        var resultEllipse: RotatedEllipse? = null

        try {
            val ellipseParamsArray: FloatArray? =
                EllipseFitterJNI.fitEllipseNative(
                    pointsX,
                    pointsY,
                    BuildConfig.DEBUG || logRegardless,
                )

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
                    log(
                        tag = SkewedEllipseShape::class.java.simpleName,
                        messageBuilder = { "Native method returned invalid ellipse radii: rX=$radiusX, rY=$radiusY" },
                        logType = LogType.WARN,
                        logRegardless = logRegardless,
                    )
                }
            } else {
                log(
                    tag = SkewedEllipseShape::class.java.simpleName,
                    messageBuilder = {
                        "Native method 'fitEllipseNative' returned null or an array of unexpected size: " +
                            "${ellipseParamsArray?.size ?: "null"}. Expected $MAX_ELLIPSE_PARAMS."
                    },
                    logType = LogType.WARN,
                    logRegardless = logRegardless,
                )
            }
        } catch (e: UnsatisfiedLinkError) {
            log(
                tag = SkewedEllipseShape::class.java.simpleName,
                messageBuilder = { "JNI UnsatisfiedLinkError in findEllipseUsingJNI: ${e.message}" },
                logType = LogType.ERROR,
                logRegardless = logRegardless,
            )
        } catch (
            @Suppress("TooGenericExceptionCaught") e: Exception,
        ) {
            log(
                tag = SkewedEllipseShape::class.java.simpleName,
                messageBuilder = { "Exception during JNI ellipse fitting: ${e.message}" },
                logType = LogType.ERROR,
                logRegardless = logRegardless,
            )
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
        private const val PI_SIXTH = (PI / 6).toFloat()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SkewedEllipseShape

        if (color != other.color) return false
        if (strokeWidth != other.strokeWidth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + strokeWidth.hashCode()
        return result
    }

    override fun toString(): String = "SkewedEllipseShape(color=$color, strokeWidth=$strokeWidth)"
}
