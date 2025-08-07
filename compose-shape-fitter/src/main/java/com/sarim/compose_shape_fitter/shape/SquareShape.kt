package com.sarim.compose_shape_fitter.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.sarim.compose_shape_fitter.shape.DrawableShape.Companion.DEFAULT_IN_PREVIEW_MODE
import com.sarim.compose_shape_fitter.shape.DrawableShape.Companion.DEFAULT_LOG_REGARDLESS
import kotlin.math.max

class SquareShape(
    val color: Color,
    val strokeWidth: Float,
    override var logRegardless: Boolean = DEFAULT_LOG_REGARDLESS,
    override var inPreviewMode: Boolean = DEFAULT_IN_PREVIEW_MODE,
) : DrawableShape {
    private fun findSmallestEnclosingSquare(points: List<Offset>): RectangleShape.Rectangle? {
        if (points.isEmpty()) {
            return null
        }

        var square: RectangleShape.Rectangle? = null
        val enclosingRectangle = findSmallestEnclosingRectangle(points)

        if (enclosingRectangle != null) {
            val squareSideLength = max(enclosingRectangle.width, enclosingRectangle.height)

            if (squareSideLength <= 0f) {
                square =
                    RectangleShape.Rectangle(
                        enclosingRectangle.topLeft,
                        enclosingRectangle.topLeft,
                    )
            } else {
                val rectCenter = enclosingRectangle.center

                val squareTopLeftX = rectCenter.x - squareSideLength / 2
                val squareTopLeftY = rectCenter.y - squareSideLength / 2

                val squareBottomRightX = squareTopLeftX + squareSideLength
                val squareBottomRightY = squareTopLeftY + squareSideLength

                square =
                    RectangleShape.Rectangle(
                        topLeft = Offset(squareTopLeftX, squareTopLeftY),
                        bottomRight = Offset(squareBottomRightX, squareBottomRightY),
                    )
            }
        }
        return square
    }

    override fun draw(
        drawScope: DrawScope,
        points: List<Offset>,
    ) {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SquareShape

        if (color != other.color) return false
        if (strokeWidth != other.strokeWidth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + strokeWidth.hashCode()
        return result
    }

    override fun toString(): String = "SquareShape(color=$color, strokeWidth=$strokeWidth)"
}
