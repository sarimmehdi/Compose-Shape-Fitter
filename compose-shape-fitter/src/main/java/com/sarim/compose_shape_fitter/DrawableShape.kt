package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope

sealed interface ApproximatedShape

sealed interface DrawableShape {
    fun draw(
        drawScope: DrawScope,
        points: List<Offset>,
    )

    fun getApproximatedShape(points: List<Offset>): ApproximatedShape?
}
