package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope

interface DrawableShape {
    fun draw(drawScope: DrawScope, points: List<Offset>)
}