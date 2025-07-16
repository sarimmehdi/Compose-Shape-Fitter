package com.sarim.compose_shape_fiiter

import androidx.compose.ui.geometry.Offset

internal data class Triangle(val p1: Offset, val p2: Offset, val p3: Offset)

internal fun findSmallestEnclosingTriangle(points: List<Offset>): Triangle? {
    if (points.size < 2) { // Allow drawing a line-like triangle for 2 points, or a point for 1.
        // Or return null if a visually distinct triangle is strictly needed.
        // For simplicity, returning null if not enough points to form a clear area.
        // Adjust based on how you want to handle these edge cases.
        if (points.isEmpty()) return null
        // If you want to handle 1 or 2 points by drawing something:
        // if (points.size == 1) return Triangle(points[0], points[0], points[0])
        // if (points.size == 2) return Triangle(points[0], points[1], points[1]) // Example: a line
        return null
    }

    val boundingRectangle = findSmallestEnclosingRectangle(points)
        ?: return null // If points was empty or findSmallestEnclosingRectangle returned null

    val topLeft = boundingRectangle.topLeft
    val topRight = Offset(boundingRectangle.bottomRight.x, boundingRectangle.topLeft.y)
    val bottomRight = boundingRectangle.bottomRight

    val midBottom = Offset((topLeft.x + topRight.x) / 2, bottomRight.y)
    return Triangle(topLeft, topRight, midBottom)
}