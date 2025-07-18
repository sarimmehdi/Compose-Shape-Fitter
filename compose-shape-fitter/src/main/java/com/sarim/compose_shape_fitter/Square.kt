package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.max

class SquareShape(val color: Color, val strokeWidth: Float) : DrawableShape {
    private fun findSmallestEnclosingSquare(points: List<Offset>): RectangleShape.Rectangle? {
        if (points.isEmpty()) {
            return null
        }

        val enclosingRectangle = findSmallestEnclosingRectangle(points)
            ?: return null

        val squareSideLength = max(enclosingRectangle.width, enclosingRectangle.height)

        if (squareSideLength <= 0f) {
            return RectangleShape.Rectangle(enclosingRectangle.topLeft, enclosingRectangle.topLeft)
        }

        val rectCenter = enclosingRectangle.center

        val squareTopLeftX = rectCenter.x - squareSideLength / 2
        val squareTopLeftY = rectCenter.y - squareSideLength / 2

        val squareBottomRightX = squareTopLeftX + squareSideLength
        val squareBottomRightY = squareTopLeftY + squareSideLength

        return RectangleShape.Rectangle(
            topLeft = Offset(squareTopLeftX, squareTopLeftY),
            bottomRight = Offset(squareBottomRightX, squareBottomRightY)
        )
    }

    override fun draw(drawScope: DrawScope, points: List<Offset>) {
        findSmallestEnclosingSquare(points)?.let { square ->
            drawScope.drawRect(
                color = color,
                topLeft = square.topLeft,
                size = Size(square.width, square.height),
                style = Stroke(width = strokeWidth),
            )
        }
    }

    override fun getApproximatedShape(points: List<Offset>) = findSmallestEnclosingSquare(points)
}

