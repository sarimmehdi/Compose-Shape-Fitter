package com.sarim.compose_shape_fitter

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path // Keep for potential direct path drawing if needed
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
// import androidx.compose.ui.graphics.drawscope.rotate // Keep if some shapes need it directly
import androidx.compose.ui.input.pointer.pointerInput
// import kotlin.math.PI // Keep if some shapes need it

@Composable
fun DrawingScreen(
    drawableShape: DrawableShape, // Accepts an instance of DrawableShape
    modifier: Modifier = Modifier,
    onPointsChange: (List<Offset>) -> Unit = {}, // Callback when points are finalized
    drawingLineColor: Color = Color.Black,
    strokeWidth: Float = 5f,
    strokeCap: StrokeCap = StrokeCap.Round
) {
    var lines by remember { mutableStateOf<List<Pair<Offset, Offset>>>(emptyList()) }
    var points by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var isDragging by remember { mutableStateOf(false) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        lines = emptyList()
                        points = emptyList()
                        // Optional: If the parent needs to know drawing started with cleared points
                        // onPointsChange(emptyList())
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val start = change.position - dragAmount
                        val end = change.position
                        lines = lines + (start to end)
                        points = points + end
                        // Optional: If the parent needs live updates of points during drag
                        // onPointsChange(points)
                    },
                    onDragEnd = {
                        isDragging = false
                        lines = emptyList() // Clear temporary drag lines
                        onPointsChange(points) // Notify parent of the final set of points
                    }
                )
            }
    ) {
        if (isDragging) {
            lines.forEach { line ->
                drawLine(
                    color = drawingLineColor,
                    start = line.first,
                    end = line.second,
                    strokeWidth = strokeWidth, // Use parameter
                    cap = strokeCap        // Use parameter
                )
            }
        } else {
            // When not dragging, if there are points, use the drawableShape to draw
            if (points.isNotEmpty()) {
                // Delegate drawing to the provided drawableShape instance
                drawableShape.draw(
                    drawScope = this, // 'this' is the DrawScope in the Canvas lambda
                    points = points
                )
            }
        }
    }
}
