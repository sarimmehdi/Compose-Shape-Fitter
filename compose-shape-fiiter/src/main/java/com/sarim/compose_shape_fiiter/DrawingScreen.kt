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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.PI

sealed interface ShapeType {
    data class Ellipse(
        val type: Type = Type.CIRCLE
    ) : ShapeType {

        companion object {
            enum class Type {
                CIRCLE, AXIS_ALIGNED_ELLIPSE, SKEWED_ELLIPSE
            }
        }
    }
    data class Rectangle(val isSquare: Boolean) : ShapeType
    data object Triangle : ShapeType
    data object Pentagon : ShapeType
    data object Hexagon : ShapeType
}

@Composable
fun DrawingScreen(
    shapeType: ShapeType, // Input parameter to decide which shape to draw
    modifier: Modifier = Modifier,
) {
    var lines by remember { mutableStateOf<List<Pair<Offset, Offset>>>(emptyList()) }
    var points by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var isDragging by remember { mutableStateOf(false) }

    Canvas(
        modifier =
            modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
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
            if (points.isNotEmpty()) {
                when (shapeType) {
                    is ShapeType.Ellipse -> {
                        when (shapeType.type) {
                            ShapeType.Ellipse.Companion.Type.CIRCLE -> {
                                findSmallestEnclosingCircle(points)?.let { circle ->
                                    drawCircle(
                                        color = Color.Red,
                                        radius = circle.radius,
                                        center = circle.center,
                                        style = Stroke(width = 5f),
                                    )
                                }
                            }
                            ShapeType.Ellipse.Companion.Type.AXIS_ALIGNED_ELLIPSE -> {
                                findSmallestEnclosingEllipse(points)?.let { ellipse ->
                                    drawOval(
                                        color = Color.Cyan, // Example color for ellipse
                                        topLeft = Offset(
                                            ellipse.center.x - ellipse.radiusX,
                                            ellipse.center.y - ellipse.radiusY
                                        ),
                                        size = Size(
                                            ellipse.radiusX * 2,
                                            ellipse.radiusY * 2
                                        ),
                                        style = Stroke(width = 5f)
                                    )
                                }
                            }
                            ShapeType.Ellipse.Companion.Type.SKEWED_ELLIPSE -> {
                                findSmallestEnclosingSkewedEllipse(points)?.let { rotatedEllipse ->
                                    rotate(
                                        degrees = rotatedEllipse.angleRad * (180f / PI.toFloat()), // Convert radians to degrees
                                        pivot = rotatedEllipse.center
                                    ) {
                                        drawOval(
                                            color = Color.Cyan,
                                            topLeft = Offset(
                                                rotatedEllipse.center.x - rotatedEllipse.radiusX,
                                                rotatedEllipse.center.y - rotatedEllipse.radiusY
                                            ),
                                            size = Size(
                                                rotatedEllipse.radiusX * 2,
                                                rotatedEllipse.radiusY * 2
                                            ),
                                            style = Stroke(width = 5f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is ShapeType.Rectangle -> {
                        if (shapeType.isSquare) {
                            findSmallestEnclosingSquare(points)?.let { square ->
                                drawRect(
                                    color = Color.DarkGray, // Example: different color for square
                                    topLeft = square.topLeft,
                                    size = Size(square.width, square.height), // For a square, rect.width and rect.height will be equal
                                    style = Stroke(width = 5f),
                                )
                            }
                        } else {
                            findSmallestEnclosingRectangle(points)?.let { rectangle ->
                                drawRect(
                                    color = Color.Blue, // Different color for rectangle
                                    topLeft = rectangle.topLeft,
                                    size = Size(rectangle.width, rectangle.height),
                                    style = Stroke(width = 5f),
                                )
                            }
                        }
                    }

                    is ShapeType.Triangle -> {
                        findSmallestEnclosingTriangle(points)?.let { triangle ->
                            val path = Path().apply {
                                moveTo(triangle.p1.x, triangle.p1.y) // Move to the first point
                                lineTo(triangle.p2.x, triangle.p2.y) // Draw line to the second point
                                lineTo(triangle.p3.x, triangle.p3.y) // Draw line to the third point
                                close() // Close the path to form a triangle
                            }
                            drawPath(
                                path = path,
                                color = Color.Green, // Or any color you prefer for the triangle
                                style = Stroke(width = 5f),
                            )
                        }
                    }

                    is ShapeType.Pentagon -> {
                        findSmallestEnclosingPentagon(points)?.let { pentagon ->
                            val path = Path().apply {
                                moveTo(pentagon.top.x, pentagon.top.y) // Start at the top point
                                lineTo(pentagon.topRight.x, pentagon.topRight.y) // Line to top-right shoulder
                                lineTo(pentagon.bottomRight.x, pentagon.bottomRight.y) // Line to bottom-right corner
                                lineTo(pentagon.bottomLeft.x, pentagon.bottomLeft.y) // Line to bottom-left corner
                                lineTo(pentagon.topLeft.x, pentagon.topLeft.y) // Line to top-left shoulder
                                close() // This will draw a line from pent.topLeft back to pent.top
                            }
                            drawPath(
                                path = path,
                                color = Color.Magenta, // Example color for pentagon
                                style = Stroke(width = 5f),
                            )
                        }
                    }

                    is ShapeType.Hexagon -> {
                        findSmallestEnclosingHexagon(points)?.let { hexagon ->
                            if (hexagon.vertices.size == 6) {
                                val path = Path().apply {
                                    // Move to the first vertex
                                    moveTo(hexagon.vertices[0].x, hexagon.vertices[0].y)
                                    // Draw lines to the subsequent vertices
                                    lineTo(hexagon.vertices[1].x, hexagon.vertices[1].y)
                                    lineTo(hexagon.vertices[2].x, hexagon.vertices[2].y)
                                    lineTo(hexagon.vertices[3].x, hexagon.vertices[3].y)
                                    lineTo(hexagon.vertices[4].x, hexagon.vertices[4].y)
                                    lineTo(hexagon.vertices[5].x, hexagon.vertices[5].y)
                                    // Close the path to draw the last side (from vertex 5 to vertex 0)
                                    close()
                                }
                                drawPath(
                                    path = path,
                                    color = Color.Yellow, // Example color for hexagon
                                    style = Stroke(width = 5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


