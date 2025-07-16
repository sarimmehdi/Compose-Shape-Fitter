package com.sarim.compose_shape_fiiter

import androidx.compose.ui.geometry.Offset
import kotlin.math.max

// New function for finding the smallest enclosing square
internal fun findSmallestEnclosingSquare(points: List<Offset>): Rectangle? {
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
        return Rectangle(enclosingRectangle.topLeft, enclosingRectangle.topLeft)
    }

    // 3. Determine the topLeft position of the square.
    // We want the square to enclose the original rectangle.
    // A common approach is to center the square around the center of the bounding rectangle.
    val rectCenter = enclosingRectangle.center

    val squareTopLeftX = rectCenter.x - squareSideLength / 2
    val squareTopLeftY = rectCenter.y - squareSideLength / 2

    val squareBottomRightX = squareTopLeftX + squareSideLength
    val squareBottomRightY = squareTopLeftY + squareSideLength

    return Rectangle(
        topLeft = Offset(squareTopLeftX, squareTopLeftY),
        bottomRight = Offset(squareBottomRightX, squareBottomRightY)
    )
}

