package com.sarim.compose_shape_fitter.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.sarim.compose_shape_fitter.utils.OffsetParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith

class EllipseShape(
    val color: Color,
    val strokeWidth: Float,
) : DrawableShape {
    @Parcelize
    data class Ellipse(
        val center: @WriteWith<OffsetParceler> Offset,
        val radiusX: Float,
        val radiusY: Float,
    ) : ApproximatedShape

    private fun findSmallestEnclosingEllipse(points: List<Offset>): Ellipse? {
        if (points.size < 2) {
            return null
        }

        val boundingRectangle = findSmallestEnclosingRectangle(points)
        var ellipse: Ellipse? = null

        if (boundingRectangle != null) {
            val centerX = boundingRectangle.topLeft.x + boundingRectangle.width / 2
            val centerY = boundingRectangle.topLeft.y + boundingRectangle.height / 2
            val center = Offset(centerX, centerY)

            val calculatedRadiusX = boundingRectangle.width / 2
            val calculatedRadiusY = boundingRectangle.height / 2

            if (calculatedRadiusX >= 0f && calculatedRadiusY >= 0f) {
                ellipse = Ellipse(center, calculatedRadiusX, calculatedRadiusY)
            } else {
                ellipse = Ellipse(center, 0f, 0f)
            }
        }
        return ellipse
    }

    override fun draw(
        drawScope: DrawScope,
        points: List<Offset>,
    ) {
        findSmallestEnclosingEllipse(points)?.let { ellipse ->
            drawScope.drawOval(
                color = color,
                topLeft =
                    Offset(
                        ellipse.center.x - ellipse.radiusX,
                        ellipse.center.y - ellipse.radiusY,
                    ),
                size =
                    Size(
                        ellipse.radiusX * 2,
                        ellipse.radiusY * 2,
                    ),
                style = Stroke(width = strokeWidth),
            )
        }
    }

    override fun getApproximatedShape(points: List<Offset>) = findSmallestEnclosingEllipse(points)
}
