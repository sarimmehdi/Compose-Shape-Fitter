package com.sarim.compose_shape_fitter.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.sarim.compose_shape_fitter.shape.DrawableShape.Companion.DEFAULT_LOG_REGARDLESS
import com.sarim.compose_shape_fitter.utils.OffsetParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class HexagonShape(
    val color: Color,
    val strokeWidth: Float,
    override var logRegardless: Boolean = DEFAULT_LOG_REGARDLESS,
) : DrawableShape {
    @Parcelize
    data class Hexagon(
        val center: @WriteWith<OffsetParceler> Offset,
        val vertices: List<@WriteWith<OffsetParceler> Offset>,
    ) : ApproximatedShape

    private fun findSmallestEnclosingHexagon(points: List<Offset>): Hexagon? {
        if (points.size < MIN_POINTS_FOR_HEXAGON) {
            return null
        }

        var hexagon: Hexagon? = null
        val boundingRectangle = findSmallestEnclosingRectangle(points)

        if (boundingRectangle != null) {
            val centerX = boundingRectangle.topLeft.x + boundingRectangle.width / 2
            val centerY = boundingRectangle.topLeft.y + boundingRectangle.height / 2
            val center = Offset(centerX, centerY)

            val rectWidth = boundingRectangle.width
            val rectHeight = boundingRectangle.height

            val radiusBasedOnWidth = if (rectWidth > 0) rectWidth / SQRT_3 else 0f
            val radiusBasedOnHeight = if (rectHeight > 0) rectHeight / 2f else 0f
            val radius = min(radiusBasedOnWidth, radiusBasedOnHeight)

            if (radius <= 0f) {
                hexagon = Hexagon(center, List(HEXAGON_TOTAL_VERTICES) { center })
            } else {
                val vertices = mutableListOf<Offset>()
                for (i in 0 until HEXAGON_TOTAL_VERTICES) {
                    val angleRad = (Math.PI / 2) + (i * Math.PI / (HEXAGON_TOTAL_VERTICES / 2))

                    val x = center.x + radius * cos(angleRad.toFloat())
                    val y = center.y + radius * sin(angleRad.toFloat())
                    vertices.add(Offset(x, y))
                }
                hexagon = Hexagon(center, vertices)
            }
        }

        return hexagon
    }

    override fun draw(
        drawScope: DrawScope,
        points: List<Offset>,
    ) {
        findSmallestEnclosingHexagon(points)?.let { hexagon ->
            if (hexagon.vertices.size == HEXAGON_TOTAL_VERTICES) {
                val path =
                    Path().apply {
                        moveTo(hexagon.vertices[VERT_0].x, hexagon.vertices[VERT_0].y)
                        lineTo(hexagon.vertices[VERT_1].x, hexagon.vertices[VERT_1].y)
                        lineTo(hexagon.vertices[VERT_2].x, hexagon.vertices[VERT_2].y)
                        lineTo(hexagon.vertices[VERT_3].x, hexagon.vertices[VERT_3].y)
                        lineTo(hexagon.vertices[VERT_4].x, hexagon.vertices[VERT_4].y)
                        lineTo(hexagon.vertices[VERT_5].x, hexagon.vertices[VERT_5].y)
                        close()
                    }
                drawScope.drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = strokeWidth),
                )
            }
        }
    }

    override fun getApproximatedShape(points: List<Offset>) = findSmallestEnclosingHexagon(points)

    companion object {
        private const val VERT_0 = 0
        private const val VERT_1 = 1
        private const val VERT_2 = 2
        private const val VERT_3 = 3
        private const val VERT_4 = 4
        private const val VERT_5 = 5
        private const val HEXAGON_TOTAL_VERTICES = 6
        private val SQRT_3 = sqrt(3.0f)
        private val MIN_POINTS_FOR_HEXAGON = sqrt(3.0f)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HexagonShape

        if (color != other.color) return false
        if (strokeWidth != other.strokeWidth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + strokeWidth.hashCode()
        return result
    }

    override fun toString(): String = "HexagonShape(color=$color, strokeWidth=$strokeWidth)"
}
