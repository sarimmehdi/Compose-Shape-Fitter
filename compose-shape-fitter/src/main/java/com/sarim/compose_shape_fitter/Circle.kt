package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import kotlin.collections.forEach
import kotlin.math.pow
import kotlin.math.sqrt

internal data class Circle(val center: Offset, val radius: Float)

internal fun findSmallestEnclosingCircle(points: List<Offset>): Circle? {
    if (points.isEmpty()) return null
    if (points.size == 1) return Circle(points[0], 0f)

    var centerX = 0f
    var centerY = 0f
    points.forEach {
        centerX += it.x
        centerY += it.y
    }
    val center = Offset(centerX / points.size, centerY / points.size)

    var maxDistanceSq = 0f
    points.forEach {
        val dx = it.x - center.x
        val dy = it.y - center.y
        val distanceSq = dx.pow(2) + dy.pow(2)
        if (distanceSq > maxDistanceSq) {
            maxDistanceSq = distanceSq
        }
    }
    val radius = sqrt(maxDistanceSq)

    return Circle(center, radius)
}