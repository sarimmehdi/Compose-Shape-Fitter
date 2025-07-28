package com.sarim.compose_shape_fitter.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.sarim.compose_shape_fitter.utils.OffsetParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import kotlin.collections.forEach
import kotlin.math.pow
import kotlin.math.sqrt

class CircleShape(
    val color: Color,
    val strokeWidth: Float,
) : DrawableShape {
    @Parcelize
    data class Circle(
        val center: @WriteWith<OffsetParceler> Offset,
        val radius: Float,
    ) : ApproximatedShape

    private fun findSmallestEnclosingCircle(points: List<Offset>): Circle? {
        var circle: Circle?

        if (points.isEmpty()) {
            return null
        } else if (points.size == 1) {
            circle = Circle(points[0], 0f)
        } else {
            var sumX = 0f
            var sumY = 0f
            points.forEach {
                sumX += it.x
                sumY += it.y
            }
            val center = Offset(sumX / points.size, sumY / points.size)

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
            circle = Circle(center, radius)
        }

        return circle
    }

    override fun draw(
        drawScope: DrawScope,
        points: List<Offset>,
    ) {
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
