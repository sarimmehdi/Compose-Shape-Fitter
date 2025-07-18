package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

class PentagonShape(val color: Color, val strokeWidth: Float) : DrawableShape {
    data class Pentagon(
        val top: Offset,
        val topLeft: Offset,
        val topRight: Offset,
        val bottomLeft: Offset,
        val bottomRight: Offset
    ) : ApproximatedShape

    private fun findSmallestEnclosingPentagon(points: List<Offset>,): Pentagon? {
        if (points.size < 3) {
            return null
        }

        val boundingRectangle = findSmallestEnclosingRectangle(points)
            ?: return null

        val rectTopLeft = boundingRectangle.topLeft
        val rectTopRight = Offset(boundingRectangle.bottomRight.x, boundingRectangle.topLeft.y)
        val rectBottomLeft = Offset(boundingRectangle.topLeft.x, boundingRectangle.bottomRight.y)
        val rectBottomRight = boundingRectangle.bottomRight
        val rectWidth = boundingRectangle.width
        val rectHeight = boundingRectangle.height

        val pentagonTop = Offset(rectTopLeft.x + rectWidth / 2, rectTopLeft.y)

        val shoulderHorizontalInsetRatio = 0.2f
        val shoulderVerticalDropRatio = 0.35f

        val pentagonTopLeftShoulder = Offset(
            rectTopLeft.x + rectWidth * shoulderHorizontalInsetRatio,
            rectTopLeft.y + rectHeight * shoulderVerticalDropRatio
        )

        val pentagonTopRightShoulder = Offset(
            rectTopRight.x - rectWidth * shoulderHorizontalInsetRatio,
            rectTopRight.y + rectHeight * shoulderVerticalDropRatio
        )

        val pentagonBottomLeft = rectBottomLeft
        val pentagonBottomRight = rectBottomRight

        return Pentagon(
            top = pentagonTop,
            topLeft = pentagonTopLeftShoulder,
            topRight = pentagonTopRightShoulder,
            bottomLeft = pentagonBottomLeft,
            bottomRight = pentagonBottomRight
        )
    }

    override fun draw(drawScope: DrawScope, points: List<Offset>) {
        findSmallestEnclosingPentagon(points)?.let { pentagon ->
            val path = Path().apply {
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
}
