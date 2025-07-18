package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

class TriangleShape(val color: Color, val strokeWidth: Float) : DrawableShape {
    data class Triangle(val p1: Offset, val p2: Offset, val p3: Offset) : ApproximatedShape

    private fun findSmallestEnclosingTriangle(points: List<Offset>): Triangle? {
        if (points.size < 2) {
            if (points.isEmpty()) return null
            return null
        }

        val boundingRectangle = findSmallestEnclosingRectangle(points)
            ?: return null

        val topLeft = boundingRectangle.topLeft
        val topRight = Offset(boundingRectangle.bottomRight.x, boundingRectangle.topLeft.y)
        val bottomRight = boundingRectangle.bottomRight

        val midBottom = Offset((topLeft.x + topRight.x) / 2, bottomRight.y)
        return Triangle(topLeft, topRight, midBottom)
    }

    override fun draw(drawScope: DrawScope, points: List<Offset>) {
        findSmallestEnclosingTriangle(points)?.let { triangle ->
            val path = Path().apply {
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
}