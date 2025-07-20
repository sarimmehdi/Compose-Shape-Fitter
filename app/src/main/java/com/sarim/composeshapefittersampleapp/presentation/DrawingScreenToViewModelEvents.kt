package com.sarim.composeshapefittersampleapp.presentation

import androidx.compose.ui.geometry.Offset
import com.sarim.compose_shape_fitter.shape.ApproximatedShape
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import kotlinx.collections.immutable.ImmutableList

sealed interface DrawingScreenToViewModelEvents {

    data class SetSelectedShape(val selectedShape: Shape) : DrawingScreenToViewModelEvents
    data class SetDragging(val isDragging: Boolean) : DrawingScreenToViewModelEvents
    data class SetLines(val lines: ImmutableList<Pair<Offset, Offset>>) : DrawingScreenToViewModelEvents
    data class UpdateLines(val lines: ImmutableList<Pair<Offset, Offset>>) : DrawingScreenToViewModelEvents
    data class SetPoints(val points: ImmutableList<Offset>) : DrawingScreenToViewModelEvents
    data class UpdatePoints(val points: ImmutableList<Offset>) : DrawingScreenToViewModelEvents
    data class SetApproximateShape(val approximatedShape: ApproximatedShape?) : DrawingScreenToViewModelEvents
}