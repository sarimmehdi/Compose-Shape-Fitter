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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun DrawingScreen(
    drawableShape: DrawableShape,
    modifier: Modifier = Modifier,
    config: Config = Config(),
    onEvent: (Event) -> Unit = {},
) {
    var lines by remember { mutableStateOf<List<Pair<Offset, Offset>>>(emptyList()) }
    var points by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var isDragging by remember { mutableStateOf(false) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        lines = emptyList()
                        points = listOf(offset)
                        if (config.liveUpdateOfPoints) {
                            onEvent(Event.PointsChangedEvent(points))
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val start = change.position - dragAmount
                        val end = change.position
                        lines = lines + (start to end)
                        points = points + end
                        if (config.liveUpdateOfPoints) {
                            onEvent(Event.PointsChangedEvent(points))
                        }
                    },
                    onDragEnd = {
                        isDragging = false

                        onEvent(Event.PointsChangedEvent(points))

                        if (points.isNotEmpty()) {
                            onEvent(Event.ApproximateShapeChangedEvent(drawableShape.getApproximatedShape(points)))
                        }
                    }
                )
            }
    ) {
        if (isDragging && config.showFingerTracedLines) {
            lines.forEach { line ->
                drawLine(
                    color = config.drawingLineColor,
                    start = line.first,
                    end = line.second,
                    strokeWidth = config.strokeWidth,
                    cap = config.strokeCap
                )
            }
        }
        else if (!isDragging && points.isNotEmpty() && config.showApproximatedShape) {
            drawableShape.draw(
                drawScope = this,
                points = points
            )
        }
    }
}


data class Config(
    val showFingerTracedLines: Boolean = true,
    val showApproximatedShape: Boolean = true,
    val liveUpdateOfPoints: Boolean = false,
    val drawingLineColor: Color = Color.Black,
    val strokeWidth: Float = 5f,
    val strokeCap: StrokeCap = StrokeCap.Round
)

sealed interface Event {
    data class PointsChangedEvent(val points: List<Offset>) : Event
    data class ApproximateShapeChangedEvent(val approximateShape: ApproximatedShape?) : Event
}
