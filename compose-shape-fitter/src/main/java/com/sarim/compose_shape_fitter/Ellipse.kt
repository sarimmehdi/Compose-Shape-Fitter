package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

class EllipseShape(val color: Color, val strokeWidth: Float) : DrawableShape {
    data class Ellipse(
        val center: Offset,
        val radiusX: Float,
        val radiusY: Float
    ) : ApproximatedShape

    private fun findSmallestEnclosingEllipse(points: List<Offset>): Ellipse? {
        if (points.size < 2) {
            return null
        }

        val boundingRectangle = findSmallestEnclosingRectangle(points)
            ?: return null

        val centerX = boundingRectangle.topLeft.x + boundingRectangle.width / 2
        val centerY = boundingRectangle.topLeft.y + boundingRectangle.height / 2
        val center = Offset(centerX, centerY)

        val radiusX = boundingRectangle.width / 2

        val radiusY = boundingRectangle.height / 2

        if (radiusX < 0f || radiusY < 0f) {
            return Ellipse(center, 0f, 0f)
        }

        return Ellipse(center, radiusX, radiusY)
    }

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

    override fun getApproximatedShape(points: List<Offset>) = findSmallestEnclosingEllipse(points)
}
