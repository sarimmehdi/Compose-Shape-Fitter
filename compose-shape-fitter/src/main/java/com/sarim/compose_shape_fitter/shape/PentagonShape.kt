package com.sarim.compose_shape_fitter.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.sarim.compose_shape_fitter.shape.DrawableShape.Companion.DEFAULT_LOG_REGARDLESS
import com.sarim.compose_shape_fitter.utils.OffsetParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith

class PentagonShape(
    val color: Color,
    val strokeWidth: Float,
    override var logRegardless: Boolean = DEFAULT_LOG_REGARDLESS,
) : DrawableShape {
    @Parcelize
    data class Pentagon(
        val top: @WriteWith<OffsetParceler> Offset,
        val topLeft: @WriteWith<OffsetParceler> Offset,
        val topRight: @WriteWith<OffsetParceler> Offset,
        val bottomLeft: @WriteWith<OffsetParceler> Offset,
        val bottomRight: @WriteWith<OffsetParceler> Offset,
    ) : ApproximatedShape

    private fun findSmallestEnclosingPentagon(points: List<Offset>): Pentagon? {
        if (points.size < MIN_POINTS_FOR_PENTAGON) {
            return null
        }

        val boundingRectangle = findSmallestEnclosingRectangle(points)
        var pentagon: Pentagon? = null

        if (boundingRectangle != null) {
            val rectTopLeft = boundingRectangle.topLeft
            val rectTopRight =
                Offset(
                    boundingRectangle.topLeft.x + boundingRectangle.width,
                    boundingRectangle.topLeft.y,
                )
            val rectBottomLeft =
                Offset(
                    boundingRectangle.topLeft.x,
                    boundingRectangle.topLeft.y + boundingRectangle.height,
                )
            val rectBottomRight =
                Offset(
                    boundingRectangle.topLeft.x + boundingRectangle.width,
                    boundingRectangle.topLeft.y + boundingRectangle.height,
                )
            val rectWidth = boundingRectangle.width
            val rectHeight = boundingRectangle.height

            val pentagonTop = Offset(rectTopLeft.x + rectWidth / 2, rectTopLeft.y)

            val pentagonTopLeftShoulder =
                Offset(
                    rectTopLeft.x + rectWidth * SHOULDER_HOR_INSET_RATIO,
                    rectTopLeft.y + rectHeight * SHOULDER_VER_DROP_RATIO,
                )

            val pentagonTopRightShoulder =
                Offset(
                    rectTopRight.x - rectWidth * SHOULDER_HOR_INSET_RATIO,
                    rectTopRight.y + rectHeight * SHOULDER_VER_DROP_RATIO,
                )

            val pentagonBottomLeft = rectBottomLeft
            val pentagonBottomRight = rectBottomRight

            pentagon =
                Pentagon(
                    top = pentagonTop,
                    topLeft = pentagonTopLeftShoulder,
                    topRight = pentagonTopRightShoulder,
                    bottomLeft = pentagonBottomLeft,
                    bottomRight = pentagonBottomRight,
                )
        }

        return pentagon
    }

    override fun draw(
        drawScope: DrawScope,
        points: List<Offset>,
    ) {
        findSmallestEnclosingPentagon(points)?.let { pentagon ->
            val path =
                Path().apply {
                    moveTo(pentagon.top.x, pentagon.top.y)
                    lineTo(pentagon.topRight.x, pentagon.topRight.y)
                    lineTo(pentagon.bottomRight.x, pentagon.bottomRight.y)
                    lineTo(pentagon.bottomLeft.x, pentagon.bottomLeft.y)
                    lineTo(pentagon.topLeft.x, pentagon.topLeft.y)
                    close()
                }
            drawScope.drawPath(
                path = path,
                color = color,
                style = Stroke(width = strokeWidth),
            )
        }
    }

    override fun getApproximatedShape(points: List<Offset>) = findSmallestEnclosingPentagon(points)

    companion object {
        private const val SHOULDER_HOR_INSET_RATIO = 0.2f
        private const val SHOULDER_VER_DROP_RATIO = 0.35f
        private const val MIN_POINTS_FOR_PENTAGON = 3
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PentagonShape

        if (color != other.color) return false
        if (strokeWidth != other.strokeWidth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + strokeWidth.hashCode()
        return result
    }

    override fun toString(): String = "PentagonShape(color=$color, strokeWidth=$strokeWidth)"
}
