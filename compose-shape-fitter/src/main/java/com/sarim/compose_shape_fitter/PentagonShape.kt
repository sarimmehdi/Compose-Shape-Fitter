package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

class PentagonShape(
    val color: Color,
    val strokeWidth: Float,
) : DrawableShape {
    data class Pentagon(
        val top: Offset,
        val topLeft: Offset,
        val topRight: Offset,
        val bottomLeft: Offset,
        val bottomRight: Offset,
    ) : ApproximatedShape

    private fun findSmallestEnclosingPentagon(points: List<Offset>): Pentagon? {
        if (points.size < MIN_POINTS_FOR_PENTAGON) {
            return null
        }

        val boundingRectangle = findSmallestEnclosingRectangle(points)
        var pentagon: Pentagon? = null

        if (boundingRectangle != null) {
            val rectTopLeft = boundingRectangle.topLeft
            val rectTopRight = Offset(
                boundingRectangle.topLeft.x + boundingRectangle.width,
                boundingRectangle.topLeft.y
            )
            val rectBottomLeft = Offset(
                boundingRectangle.topLeft.x,
                boundingRectangle.topLeft.y + boundingRectangle.height
            )
            val rectBottomRight = Offset(
                boundingRectangle.topLeft.x + boundingRectangle.width,
                boundingRectangle.topLeft.y + boundingRectangle.height
            )
            val rectWidth = boundingRectangle.width
            val rectHeight = boundingRectangle.height

            val pentagonTop = Offset(rectTopLeft.x + rectWidth / 2, rectTopLeft.y)

            val pentagonTopLeftShoulder =
                Offset(
                    rectTopLeft.x + rectWidth * SHOULDER_HOR_INSET_RATIO,
                    rectTopLeft.y + rectHeight * SHOULDER_VER_DROP_RATIO,
                )

            val pentagonTopRightShoulder =
                Offset(
                    rectTopRight.x - rectWidth * SHOULDER_HOR_INSET_RATIO,
                    rectTopRight.y + rectHeight * SHOULDER_VER_DROP_RATIO,
                )

            val pentagonBottomLeft = rectBottomLeft
            val pentagonBottomRight = rectBottomRight

            pentagon = Pentagon(
                top = pentagonTop,
                topLeft = pentagonTopLeftShoulder,
                topRight = pentagonTopRightShoulder,
                bottomLeft = pentagonBottomLeft,
                bottomRight = pentagonBottomRight,
            )
        }

        return pentagon
    }

    override fun draw(
        drawScope: DrawScope,
        points: List<Offset>,
    ) {
        findSmallestEnclosingPentagon(points)?.let { pentagon ->
            val path =
                Path().apply {
                    moveTo(pentagon.top.x, pentagon.top.y)
                    lineTo(pentagon.topRight.x, pentagon.topRight.y)
                    lineTo(pentagon.bottomRight.x, pentagon.bottomRight.y)
                    lineTo(pentagon.bottomLeft.x, pentagon.bottomLeft.y)
                    lineTo(pentagon.topLeft.x, pentagon.topLeft.y)
                    close()
                }
            drawScope.drawPath(
                path = path,
                color = color,
                style = Stroke(width = strokeWidth),
            )
        }
    }

    override fun getApproximatedShape(points: List<Offset>) = findSmallestEnclosingPentagon(points)

    companion object {
        private const val SHOULDER_HOR_INSET_RATIO = 0.2f
        private const val SHOULDER_VER_DROP_RATIO = 0.35f
        private const val MIN_POINTS_FOR_PENTAGON = 3
    }
}
