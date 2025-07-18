package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI

internal data class Triangle(val p1: Offset, val p2: Offset, val p3: Offset)

internal fun findSmallestEnclosingTriangle(points: List<Offset>): Triangle? {
    if (points.size < 2) { // Allow drawing a line-like triangle for 2 points, or a point for 1.
        // Or return null if a visually distinct triangle is strictly needed.
        // For simplicity, returning null if not enough points to form a clear area.
        // Adjust based on how you want to handle these edge cases.
        if (points.isEmpty()) return null
        // If you want to handle 1 or 2 points by drawing something:
        // if (points.size == 1) return Triangle(points[0], points[0], points[0])
        // if (points.size == 2) return Triangle(points[0], points[1], points[1]) // Example: a line
        return null
    }

    val boundingRectangle = findSmallestEnclosingRectangle(points)
        ?: return null // If points was empty or findSmallestEnclosingRectangle returned null

    val topLeft = boundingRectangle.topLeft
    val topRight = Offset(boundingRectangle.bottomRight.x, boundingRectangle.topLeft.y)
    val bottomRight = boundingRectangle.bottomRight

    val midBottom = Offset((topLeft.x + topRight.x) / 2, bottomRight.y)
    return Triangle(topLeft, topRight, midBottom)
}

class TriangleShape(val color: Color, val strokeWidth: Float) : DrawableShape {
    override fun draw(drawScope: DrawScope, points: List<Offset>) {
        findSmallestEnclosingTriangle(points)?.let { triangle ->
            val path = Path().apply {
                moveTo(triangle.p1.x, triangle.p1.y) // Move to the first point
                lineTo(triangle.p2.x, triangle.p2.y) // Draw line to the second point
                lineTo(triangle.p3.x, triangle.p3.y) // Draw line to the third point
                close() // Close the path to form a triangle
            }
            drawScope.drawPath(
                path = path,
                color = color, // Or any color you prefer for the triangle
                style = Stroke(width = strokeWidth),
            )
        }
    }
}