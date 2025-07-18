package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.collections.forEach
import kotlin.math.pow
import kotlin.math.sqrt

class CircleShape(val color: Color, val strokeWidth: Float) : DrawableShape {
    data class Circle(val center: Offset, val radius: Float) : ApproximatedShape

    private fun findSmallestEnclosingCircle(points: List<Offset>): Circle? {
        if (points.isEmpty()) return null
        if (points.size == 1) return Circle(points[0], 0f)

        var centerX = 0f
        var centerY = 0f
        points.forEach {
            centerX += it.x
            centerY += it.y
        }
        val center = Offset(centerX / points.size, centerY / points.size)

        var maxDistanceSq = 0f
        points.forEach {
            val dx = it.x - center.x
            val dy = it.y - center.y
            val distanceSq = dx.pow(2) + dy.pow(2)
            if (distanceSq > maxDistanceSq) {
                maxDistanceSq = distanceSq
            }
        }
        val radius = sqrt(maxDistanceSq)

        return Circle(center, radius)
    }

    override fun draw(drawScope: DrawScope, points: List<Offset>) {
        findSmallestEnclosingCircle(points)?.let { circle ->
            drawScope.drawCircle(
                color = color,
                radius = circle.radius,
                center = circle.center,
                style = Stroke(width = strokeWidth),
            )
        }
    }

    override fun getApproximatedShape(points: List<Offset>) = findSmallestEnclosingCircle(points)
}