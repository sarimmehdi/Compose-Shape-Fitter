package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

// Data class to represent an ellipse
data class Ellipse(
    val center: Offset,
    val radiusX: Float, // Horizontal radius
    val radiusY: Float  // Vertical radius
)

fun findSmallestEnclosingEllipse(points: List<Offset>): Ellipse? {
    if (points.size < 2) {
        // Not enough points to define a meaningful ellipse area.
        // For a single point, you could return an ellipse with zero radii (a point),
        // but for simplicity, returning null for less than 2 points.
        return null
    }

    // 1. Find the bounding rectangle of the points.
    val boundingRectangle = findSmallestEnclosingRectangle(points)
        ?: return null // Should not happen if points.size >= 2

    // 2. Calculate the properties of the ellipse from the bounding rectangle.
    // The center of the ellipse will be the center of the bounding rectangle.
    val centerX = boundingRectangle.topLeft.x + boundingRectangle.width / 2
    val centerY = boundingRectangle.topLeft.y + boundingRectangle.height / 2
    val center = Offset(centerX, centerY)

    // The horizontal radius (radiusX) is half the width of the bounding rectangle.
    val radiusX = boundingRectangle.width / 2

    // The vertical radius (radiusY) is half the height of the bounding rectangle.
    val radiusY = boundingRectangle.height / 2

    // Ensure radii are not negative (can happen if width/height is 0)
    if (radiusX < 0f || radiusY < 0f) {
        return Ellipse(center, 0f, 0f) // Or handle as an error/special case
    }

    return Ellipse(center, radiusX, radiusY)
}

class EllipseShape(val color: Color, val strokeWidth: Float) : DrawableShape {
    override fun draw(drawScope: DrawScope, points: List<Offset>) {
        findSmallestEnclosingEllipse(points)?.let { ellipse ->
            drawScope.drawOval(
                color = color,
                topLeft = Offset(
                    ellipse.center.x - ellipse.radiusX,
                    ellipse.center.y - ellipse.radiusY
                ),
                size = Size(
                    ellipse.radiusX * 2,
                    ellipse.radiusY * 2
                ),
                style = Stroke(width = strokeWidth)
            )
        }
    }
}
