package com.sarim.compose_shape_fitter.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.sarim.compose_shape_fitter.shape.DrawableShape.Companion.DEFAULT_IN_PREVIEW_MODE
import com.sarim.compose_shape_fitter.shape.DrawableShape.Companion.DEFAULT_LOG_REGARDLESS
import com.sarim.compose_shape_fitter.utils.OffsetParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith

class TriangleShape(
    val color: Color,
    val strokeWidth: Float,
    override var logRegardless: Boolean = DEFAULT_LOG_REGARDLESS,
    override var inPreviewMode: Boolean = DEFAULT_IN_PREVIEW_MODE,
) : DrawableShape {
    @Parcelize
    data class Triangle(
        val p1: @WriteWith<OffsetParceler> Offset,
        val p2: @WriteWith<OffsetParceler> Offset,
        val p3: @WriteWith<OffsetParceler> Offset,
    ) : ApproximatedShape

    private fun findSmallestEnclosingTriangle(points: List<Offset>): Triangle? {
        if (points.size < 2) {
            return null
        }

        val boundingRectangle = findSmallestEnclosingRectangle(points)
        var triangle: Triangle? = null

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

            val topMiddle = Offset((rectTopLeft.x + rectTopRight.x) / 2, rectTopLeft.y)
            val bottomLeft = rectBottomLeft
            val bottomRight = Offset(rectTopRight.x, rectBottomLeft.y)

            triangle = Triangle(topMiddle, bottomLeft, bottomRight)
        }
        return triangle
    }

    override fun draw(
        drawScope: DrawScope,
        points: List<Offset>,
    ) {
        findSmallestEnclosingTriangle(points)?.let { triangle ->
            val path =
                Path().apply {
                    moveTo(triangle.p1.x, triangle.p1.y)
                    lineTo(triangle.p2.x, triangle.p2.y)
                    lineTo(triangle.p3.x, triangle.p3.y)
                    close()
                }
            drawScope.drawPath(
                path = path,
                color = color,
                style = Stroke(width = strokeWidth),
            )
        }
    }

    override fun getApproximatedShape(points: List<Offset>) = findSmallestEnclosingTriangle(points)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TriangleShape

        if (color != other.color) return false
        if (strokeWidth != other.strokeWidth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + strokeWidth.hashCode()
        return result
    }

    override fun toString(): String = "TriangleShape(color=$color, strokeWidth=$strokeWidth)"
}
