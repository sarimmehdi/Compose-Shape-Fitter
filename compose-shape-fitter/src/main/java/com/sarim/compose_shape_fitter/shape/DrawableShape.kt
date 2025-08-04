package com.sarim.compose_shape_fitter.shape

import android.os.Parcelable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface ApproximatedShape : Parcelable

sealed interface DrawableShape {
    var logRegardless: Boolean

    fun draw(
        drawScope: DrawScope,
        points: List<Offset>,
    )

    fun getApproximatedShape(points: List<Offset>): ApproximatedShape?

    companion object {
        const val DEFAULT_LOG_REGARDLESS = false
    }
}
