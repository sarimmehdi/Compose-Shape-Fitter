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
                        if (config.liveUpdateOfPoints) {
                            onEvent(Event.PointsChangedEvent(points))
                        }
                    },
                    onDragEnd = {
                        isDragging = false
                        lines = emptyList()
                        onEvent(Event.PointsChangedEvent(points))
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
        } else {
            if (points.isNotEmpty()) {
                if (config.showApproximatedShape) {
                    drawableShape.draw(
                        drawScope = this,
                        points = points
                    )
                }
                onEvent(Event.ApproximateShapeChangedEvent(drawableShape.getApproximatedShape(points)))
            }
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
