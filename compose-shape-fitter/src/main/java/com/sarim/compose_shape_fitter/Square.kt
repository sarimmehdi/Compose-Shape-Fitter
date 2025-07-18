package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI
import kotlin.math.max

class SquareShape(val color: Color, val strokeWidth: Float) : DrawableShape {
    // New function for finding the smallest enclosing square
    private fun findSmallestEnclosingSquare(points: List<Offset>): RectangleShape.Rectangle? {
        if (points.isEmpty()) {
            return null
        }

        // 1. Find the smallest enclosing rectangle for the points.
        val enclosingRectangle = findSmallestEnclosingRectangle(points)
            ?: return null // Should not happen if points is not empty

        // 2. Determine the side length of the square.
        // The side length of the square must be large enough to contain the enclosing rectangle.
        // So, it will be the maximum of the rectangle's width and height.
        val squareSideLength = max(enclosingRectangle.width, enclosingRectangle.height)

        if (squareSideLength <= 0f) { // Handles cases with no area (e.g. single point or collinear points)
            return RectangleShape.Rectangle(enclosingRectangle.topLeft, enclosingRectangle.topLeft)
        }

        // 3. Determine the topLeft position of the square.
        // We want the square to enclose the original rectangle.
        // A common approach is to center the square around the center of the bounding rectangle.
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
                color = color, // Example: different color for square
                topLeft = square.topLeft,
                size = Size(square.width, square.height), // For a square, rect.width and rect.height will be equal
                style = Stroke(width = strokeWidth),
            )
        }
    }

    override fun getApproximatedShape(points: List<Offset>) = findSmallestEnclosingSquare(points)
}

