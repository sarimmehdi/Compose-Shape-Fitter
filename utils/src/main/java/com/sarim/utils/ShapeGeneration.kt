package com.sarim.utils

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val DEFAULT_RADIUS = 200

fun generateEllipsePoints(
    centerX: Float,
    centerY: Float,
    radiusX: Float,
    radiusY: Float,
    numPoints: Int = 36,
): List<Offset> {
    val points = mutableListOf<Offset>()
    for (i in 0..numPoints) {
        val angle = i * (2 * PI / numPoints)
        val x = centerX + radiusX * cos(angle).toFloat()
        val y = centerY + radiusY * sin(angle).toFloat()
        points.add(Offset(x, y))
    }
    return points
}

fun generatePolygonPoints(
    sides: Int,
    xOffset: Float = 0f,
    yOffset: Float = 0f,
): List<Offset> {
    val points = mutableListOf<Offset>()
    val angleStep = 2 * PI / sides
    for (i in 0..sides) {
        val angle = i * angleStep
        val x = xOffset + DEFAULT_RADIUS * cos(angle).toFloat()
        val y = yOffset + DEFAULT_RADIUS * sin(angle).toFloat()
        points.add(Offset(x, y))
    }
    return points
}
