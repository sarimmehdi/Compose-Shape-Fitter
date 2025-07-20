package com.sarim.composeshapefittersampleapp.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import com.sarim.compose_shape_fitter.shape.DrawableShape
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenToViewModelEvents
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@Composable
fun CanvasComponent(
    data: CanvasComponentData,
    modifier: Modifier = Modifier,
    onEvent: (DrawingScreenToViewModelEvents) -> Unit,
) {
    Canvas(
        modifier =
            modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            onEvent(DrawingScreenToViewModelEvents.SetDragging(true))
                            onEvent(DrawingScreenToViewModelEvents.SetLines(persistentListOf()))
                            onEvent(DrawingScreenToViewModelEvents.SetPoints(persistentListOf(offset)))
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val start = change.position - dragAmount
                            val end = change.position
                            onEvent(DrawingScreenToViewModelEvents.UpdateLines(
                                (data.lines + (start to end)).toPersistentList())
                            )
                            onEvent(DrawingScreenToViewModelEvents.UpdatePoints(
                                (data.points + end).toPersistentList())
                            )
                        },
                        onDragEnd = {
                            onEvent(DrawingScreenToViewModelEvents.SetDragging(false))
                            if (data.points.isNotEmpty()) {
                                onEvent(
                                    DrawingScreenToViewModelEvents.SetApproximateShape(
                                        data.drawableShape.getApproximatedShape(data.points)
                                    )
                                )
                            }
                        },
                    )
                },
    ) {
        if (data.isDragging && data.showFingerTracedLines) {
            data.lines.forEach { line ->
                drawLine(
                    color = Color.Black,
                    start = line.first,
                    end = line.second,
                    strokeWidth = 5f,
                    cap = StrokeCap.Round,
                )
            }
        } else if (!data.isDragging && data.points.isNotEmpty() && data.showApproximatedShape) {
            data.drawableShape.draw(
                drawScope = this,
                points = data.points,
            )
        }
    }
}

data class CanvasComponentData(
    val drawableShape: DrawableShape,
    val isDragging: Boolean,
    val points: ImmutableList<Offset>,
    val lines: ImmutableList<Pair<Offset, Offset>>,
    val showFingerTracedLines: Boolean,
    val showApproximatedShape: Boolean,
)
