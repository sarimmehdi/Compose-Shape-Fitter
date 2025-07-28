package com.sarim.compose_shape_fitter.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.sarim.compose_shape_fitter.utils.OffsetParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import kotlin.collections.forEach
import kotlin.math.max
import kotlin.math.min

internal fun findSmallestEnclosingRectangle(points: List<Offset>): RectangleShape.Rectangle? {
    if (points.isEmpty()) {
        return null
    }

    var minX = Float.POSITIVE_INFINITY
    var minY = Float.POSITIVE_INFINITY
    var maxX = Float.NEGATIVE_INFINITY
    var maxY = Float.NEGATIVE_INFINITY

    points.forEach { point ->
        minX = min(minX, point.x)
        minY = min(minY, point.y)
        maxX = max(maxX, point.x)
        maxY = max(maxY, point.y)
    }

    return RectangleShape.Rectangle(topLeft = Offset(minX, minY), bottomRight = Offset(maxX, maxY))
}

class RectangleShape(
    val color: Color,
    val strokeWidth: Float,
) : DrawableShape {
    @Parcelize
    data class Rectangle(
        val topLeft: @WriteWith<OffsetParceler> Offset,
        val bottomRight: @WriteWith<OffsetParceler> Offset,
    ) : ApproximatedShape {
        val width: Float
            get() = max(0f, bottomRight.x - topLeft.x)
        val height: Float
            get() = max(0f, bottomRight.y - topLeft.y)

        val center: Offset
            get() = Offset(topLeft.x + width / 2, topLeft.y + height / 2)
    }

    override fun draw(
        drawScope: DrawScope,
        points: List<Offset>,
    ) {
        findSmallestEnclosingRectangle(points)?.let { rectangle ->
            drawScope.drawRect(
                color = color,
                topLeft = rectangle.topLeft,
                size = Size(rectangle.width, rectangle.height),
                style = Stroke(width = strokeWidth),
            )
        }
    }

    override fun getApproximatedShape(points: List<Offset>) = findSmallestEnclosingRectangle(points)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RectangleShape

        if (color != other.color) return false
        if (strokeWidth != other.strokeWidth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + strokeWidth.hashCode()
        return result
    }

    override fun toString(): String {
        return "RectangleShape(color=$color, strokeWidth=$strokeWidth)"
    }
}
