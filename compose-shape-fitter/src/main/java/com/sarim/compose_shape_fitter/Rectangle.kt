package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.collections.forEach
import kotlin.math.max
import kotlin.math.min

internal data class Rectangle(val topLeft: Offset, val bottomRight: Offset) {
    val width: Float
        get() = max(0f, bottomRight.x - topLeft.x) // Ensure width is not negative
    val height: Float
        get() = max(0f, bottomRight.y - topLeft.y) // Ensure height is not negative

    val center: Offset
        get() = Offset(topLeft.x + width / 2, topLeft.y + height / 2)
}

internal fun findSmallestEnclosingRectangle(points: List<Offset>): Rectangle? {
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

    return Rectangle(topLeft = Offset(minX, minY), bottomRight = Offset(maxX, maxY))
}

class RectangleShape(val color: Color, val strokeWidth: Float) : DrawableShape {
    override fun draw(drawScope: DrawScope, points: List<Offset>) {
        findSmallestEnclosingRectangle(points)?.let { rectangle ->
            drawScope.drawRect(
                color = color, // Different color for rectangle
                topLeft = rectangle.topLeft,
                size = Size(rectangle.width, rectangle.height),
                style = Stroke(width = strokeWidth),
            )
        }
    }
}