package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.min
import kotlin.math.sqrt

class HexagonShape(val color: Color, val strokeWidth: Float) : DrawableShape {
    data class Hexagon(
        val center: Offset,
        val vertices: List<Offset>
    ) : ApproximatedShape

    private fun findSmallestEnclosingHexagon(points: List<Offset>): Hexagon? {
        if (points.size < 3) {
            return null
        }

        val boundingRectangle = findSmallestEnclosingRectangle(points)
            ?: return null

        val centerX = boundingRectangle.topLeft.x + boundingRectangle.width / 2
        val centerY = boundingRectangle.topLeft.y + boundingRectangle.height / 2
        val center = Offset(centerX, centerY)

        val rectWidth = boundingRectangle.width
        val rectHeight = boundingRectangle.height

        val radiusFromWidth = if (rectWidth > 0) rectWidth / sqrt(3f) else 0f
        val radiusFromHeight = if (rectHeight > 0) rectHeight / 2f else 0f

        val radius = min(radiusFromWidth, radiusFromHeight)

        if (radius <= 0f) {
            return Hexagon(center, List(6) { center })
        }

        val vertices = mutableListOf<Offset>()
        for (i in 0 until 6) {
            val angleRad = (Math.PI / 2) + (i * Math.PI / 3)
            val x = center.x + radius * kotlin.math.cos(angleRad.toFloat())
            val y = center.y + radius * kotlin.math.sin(angleRad.toFloat())
            vertices.add(Offset(x, y))
        }

        return Hexagon(center, vertices)
    }

    override fun draw(drawScope: DrawScope, points: List<Offset>) {
        findSmallestEnclosingHexagon(points)?.let { hexagon ->
            if (hexagon.vertices.size == 6) {
                val path = Path().apply {
                    moveTo(hexagon.vertices[0].x, hexagon.vertices[0].y)
                    // Draw lines to the subsequent vertices
                    lineTo(hexagon.vertices[1].x, hexagon.vertices[1].y)
                    lineTo(hexagon.vertices[2].x, hexagon.vertices[2].y)
                    lineTo(hexagon.vertices[3].x, hexagon.vertices[3].y)
                    lineTo(hexagon.vertices[4].x, hexagon.vertices[4].y)
                    lineTo(hexagon.vertices[5].x, hexagon.vertices[5].y)
                    close()
                }
                drawScope.drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = strokeWidth)
                )
            }
        }
    }

    override fun getApproximatedShape(points: List<Offset>) = findSmallestEnclosingHexagon(points)
}

