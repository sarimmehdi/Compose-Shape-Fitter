package com.sarim.compose_shape_fiiter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.min
import kotlin.math.min
import kotlin.math.sqrt

data class Hexagon(
    val center: Offset,
    val vertices: List<Offset> // List of 6 vertices
)

fun findSmallestEnclosingHexagon(points: List<Offset>): Hexagon? {
    if (points.size < 3) { // Need at least 3 points for a meaningful 2D area
        return null
    }

    val boundingRectangle = findSmallestEnclosingRectangle(points)
        ?: return null

    val centerX = boundingRectangle.topLeft.x + boundingRectangle.width / 2
    val centerY = boundingRectangle.topLeft.y + boundingRectangle.height / 2
    val center = Offset(centerX, centerY)

    // For a pointy-top hexagon inscribed in a rectangle:
    // The overall width of the hexagon is `2 * side` or `sqrt(3) * heightOfEquivalentEquilateralTrianglePart`
    // The overall height of the hexagon is `2 * apothem` where apothem is `sqrt(3)/2 * side`
    // OR, more simply, the height is `2 * R` if R is distance from center to top/bottom vertex.
    // And width is `sqrt(3) * R`.

    // We need to decide whether the hexagon's width or height is constrained by the bounding box.
    val rectWidth = boundingRectangle.width
    val rectHeight = boundingRectangle.height

    // Calculate the hexagon "radius" (distance from center to any vertex)
    // based on whether the width or height of the bounding box is the limiting factor.
    // For a pointy-top hexagon:
    // width = sqrt(3) * radius
    // height = 2 * radius
    // So, radius based on width: R_w = rectWidth / sqrt(3)
    // So, radius based on height: R_h = rectHeight / 2
    // We must choose the smaller of these two radii to ensure the hexagon fits.
    val radiusFromWidth = if (rectWidth > 0) rectWidth / sqrt(3f) else 0f
    val radiusFromHeight = if (rectHeight > 0) rectHeight / 2f else 0f

    val radius = min(radiusFromWidth, radiusFromHeight)

    if (radius <= 0f) { // If bounding box has no area
        // Create a degenerate hexagon at the center
        return Hexagon(center, List(6) { center })
    }

    val vertices = mutableListOf<Offset>()
    for (i in 0 until 6) {
        // Angle for pointy-top starts at 90 degrees (pointing up) or -90/270 (pointing down)
        // Let's start with top point: angle is PI/2 (90 degrees)
        // Each vertex is 60 degrees (PI/3 radians) apart.
        val angleRad = (Math.PI / 2) + (i * Math.PI / 3) // Start at top point (90 deg)
        val x = center.x + radius * kotlin.math.cos(angleRad.toFloat())
        val y = center.y + radius * kotlin.math.sin(angleRad.toFloat()) // Y typically goes down in screen coords
        vertices.add(Offset(x, y))
    }

    return Hexagon(center, vertices)
}

