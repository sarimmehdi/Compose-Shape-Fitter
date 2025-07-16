package com.sarim.compose_shape_fiiter

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

enum class ShapeType {
    CIRCLE, RECTANGLE, TRIANGLE, PENTAGON
}

@Composable
fun DrawingScreen(
    shapeType: ShapeType, // Input parameter to decide which shape to draw
    modifier: Modifier = Modifier,
) {
    var lines by remember { mutableStateOf<List<Pair<Offset, Offset>>>(emptyList()) }
    var points by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var circle by remember { mutableStateOf<Circle?>(null) }
    var rectangle by remember { mutableStateOf<Rectangle?>(null) } // State for rectangle
    var triangle by remember { mutableStateOf<Triangle?>(null) } // State for rectangle
    var pentagon by remember { mutableStateOf<Pentagon?>(null) } // State for rectangle
    var isDragging by remember { mutableStateOf(false) }

    Canvas(
        modifier =
            modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                            circle = null // Clear existing circle
                            rectangle = null // Clear existing rectangle
                            triangle = null
                            pentagon = null
                            lines = emptyList()
                            points = emptyList()
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()

                            val start = change.position - dragAmount
                            val end = change.position
                            lines = lines + (start to end)
                            points = points + end
                        },
                        onDragEnd = {
                            isDragging = false
                            if (points.isNotEmpty()) {
                                when (shapeType) {
                                    ShapeType.CIRCLE -> {
                                        circle = findSmallestEnclosingCircle(points)
                                        rectangle = null // Ensure rectangle is not drawn
                                        triangle = null
                                        pentagon = null
                                    }

                                    ShapeType.RECTANGLE -> {
                                        rectangle = findSmallestEnclosingRectangle(points)
                                        circle = null // Ensure circle is not drawn
                                        triangle = null
                                        pentagon = null
                                    }

                                    ShapeType.TRIANGLE -> {
                                        triangle = findHeuristicEnclosingTriangle(points)
                                        circle = null
                                        rectangle = null
                                        pentagon = null
                                    }

                                    ShapeType.PENTAGON -> {
                                        pentagon = findSmallestEnclosingPentagon(points)
                                        circle = null
                                        rectangle = null
                                        triangle = null
                                    }
                                }
                            }
                            lines = emptyList() // Clear lines after drawing the shape
                        },
                    )
                },
    ) {
        if (isDragging) {
            lines.forEach { line ->
                drawLine(
                    color = Color.Black,
                    start = line.first,
                    end = line.second,
                    strokeWidth = 5f,
                    cap = StrokeCap.Round,
                )
            }
        } else {
            // Draw based on the shapeType and if the shape data is available
            when (shapeType) {
                ShapeType.CIRCLE -> {
                    circle?.let {
                        drawCircle(
                            color = Color.Red,
                            radius = it.radius,
                            center = it.center,
                            style = Stroke(width = 5f),
                        )
                    }
                }
                ShapeType.RECTANGLE -> {
                    rectangle?.let { rect ->
                        drawRect(
                            color = Color.Blue, // Different color for rectangle
                            topLeft = rect.topLeft,
                            size = Size(rect.width, rect.height),
                            style = Stroke(width = 5f),
                        )
                    }
                }
                ShapeType.TRIANGLE -> {
                    triangle?.let { tri -> // 'tri' is the Triangle object
                        val path = Path().apply {
                            moveTo(tri.p1.x, tri.p1.y) // Move to the first point
                            lineTo(tri.p2.x, tri.p2.y) // Draw line to the second point
                            lineTo(tri.p3.x, tri.p3.y) // Draw line to the third point
                            close() // Close the path to form a triangle
                        }
                        drawPath(
                            path = path,
                            color = Color.Green, // Or any color you prefer for the triangle
                            style = Stroke(width = 5f),
                        )
                    }
                }
                ShapeType.PENTAGON -> { // If ShapeType is an enum
                    // is ShapeType.Pentagon -> { // If ShapeType is a sealed interface with a Pentagon data object/class
                    pentagon?.let { pent ->
                        val path = Path().apply {
                            moveTo(pent.top.x, pent.top.y) // Start at the top point
                            lineTo(pent.topRight.x, pent.topRight.y) // Line to top-right shoulder
                            lineTo(pent.bottomRight.x, pent.bottomRight.y) // Line to bottom-right corner
                            lineTo(pent.bottomLeft.x, pent.bottomLeft.y) // Line to bottom-left corner
                            lineTo(pent.topLeft.x, pent.topLeft.y) // Line to top-left shoulder
                            close() // This will draw a line from pent.topLeft back to pent.top
                        }
                        drawPath(
                            path = path,
                            color = Color.Magenta, // Example color for pentagon
                            style = Stroke(width = 5f),
                        )
                    }
                }
            }
        }
    }
}

data class Circle(val center: Offset, val radius: Float)

fun findSmallestEnclosingCircle(points: List<Offset>): Circle? {
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

data class Rectangle(val topLeft: Offset, val bottomRight: Offset) {
    val width: Float
        get() = max(0f, bottomRight.x - topLeft.x) // Ensure width is not negative
    val height: Float
        get() = max(0f, bottomRight.y - topLeft.y) // Ensure height is not negative
}

fun findSmallestEnclosingRectangle(points: List<Offset>): Rectangle? {
    if (points.isEmpty()) {
        return null
    }

    var minX = Float.POSITIVE_INFINITY
    var minY = Float.POSITIVE_INFINITY
    var maxX = Float.NEGATIVE_INFINITY
    var maxY = Float.NEGATIVE_INFINITY

    points.forEach { point ->
        minX = min(minX, point.x)
        minY = min(minY, point.y)
        maxX = max(maxX, point.x)
        maxY = max(maxY, point.y)
    }

    return Rectangle(topLeft = Offset(minX, minY), bottomRight = Offset(maxX, maxY))
}

data class Triangle(val p1: Offset, val p2: Offset, val p3: Offset)

fun findHeuristicEnclosingTriangle(points: List<Offset>): Triangle? {
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

data class Pentagon(
    val top: Offset,
    val topLeft: Offset,
    val topRight: Offset,
    val bottomLeft: Offset,
    val bottomRight: Offset
)

fun findSmallestEnclosingPentagon(points: List<Offset>): Pentagon? {
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


