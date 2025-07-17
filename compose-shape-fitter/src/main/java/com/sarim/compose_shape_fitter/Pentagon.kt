package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset

internal data class Pentagon(
    val top: Offset,
    val topLeft: Offset,
    val topRight: Offset,
    val bottomLeft: Offset,
    val bottomRight: Offset
)

internal fun findSmallestEnclosingPentagon(points: List<Offset>,): Pentagon? {
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

    // Top point of the pentagon (apex)
    val pentagonTop = Offset(rectTopLeft.x + rectWidth / 2, rectTopLeft.y)

    // "Shoulder" points of the pentagon.
    // These points will be inset from the top corners of the bounding rectangle
    // and slightly lower, creating the sloped top edges of the pentagon.

    // Define how much the shoulders are inset horizontally and vertically.
    // These ratios can be adjusted to change the pentagon's shape.
    // For example, 0.25f means the shoulder point is 25% of the width from the side,
    // and 25% of the height down from the top.
    val shoulderHorizontalInsetRatio = 0.2f // Inset from the side (e.g., 20% of width)
    val shoulderVerticalDropRatio = 0.35f  // Drop from the top (e.g., 35% of height)

    val pentagonTopLeftShoulder = Offset(
        rectTopLeft.x + rectWidth * shoulderHorizontalInsetRatio,
        rectTopLeft.y + rectHeight * shoulderVerticalDropRatio
    )

    val pentagonTopRightShoulder = Offset(
        rectTopRight.x - rectWidth * shoulderHorizontalInsetRatio,
        rectTopRight.y + rectHeight * shoulderVerticalDropRatio
    )

    // Bottom points of the pentagon remain as the bottom corners of the bounding rectangle
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
