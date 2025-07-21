package com.sarim.composeshapefittersampleapp.presentation

import android.os.Parcelable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.sarim.compose_shape_fitter.shape.ApproximatedShape
import com.sarim.compose_shape_fitter.shape.CircleShape
import com.sarim.compose_shape_fitter.shape.EllipseShape
import com.sarim.compose_shape_fitter.shape.HexagonShape
import com.sarim.compose_shape_fitter.shape.ObbShape
import com.sarim.compose_shape_fitter.shape.PentagonShape
import com.sarim.compose_shape_fitter.shape.RectangleShape
import com.sarim.compose_shape_fitter.shape.SkewedEllipseShape
import com.sarim.compose_shape_fitter.shape.SquareShape
import com.sarim.compose_shape_fitter.shape.TriangleShape
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.utils.OffsetParceler
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith

@Parcelize
data class DrawingScreenState(
    val selectedShape: Shape = Shape.Circle,
    val allShapes: ImmutableList<Shape> = persistentListOf(),
    val isDragging: Boolean = false,
    val approximatedShape: ApproximatedShape? = null,
    val points: ImmutableList<@WriteWith<OffsetParceler> Offset> = persistentListOf(),
    val lines: ImmutableList<Pair<@WriteWith<OffsetParceler> Offset, @WriteWith<OffsetParceler> Offset>> = persistentListOf(),
    val showFingerTracedLines: Boolean = true,
    val showApproximatedShape: Boolean = true,
    val liveUpdateOfPoints: Boolean = true,
    val showSettingsDropDown: Boolean = false,
) : Parcelable {
    
    fun getDrawableShape(color: Color, strokeWidth: Float) = when (selectedShape) {
        Shape.Circle -> CircleShape(
            color,
            strokeWidth,
        )
        Shape.Ellipse -> EllipseShape(
            color,
            strokeWidth,
        )
        Shape.Hexagon -> HexagonShape(
            color,
            strokeWidth,
        )
        Shape.OrientedRectangle -> ObbShape(
            color,
            strokeWidth,
            false
        )
        Shape.OrientedSquare -> ObbShape(
            color,
            strokeWidth,
            true
        )
        Shape.Pentagon -> PentagonShape(
            color,
            strokeWidth,
        )
        Shape.Rectangle -> RectangleShape(
            color,
            strokeWidth,
        )
        Shape.OrientedEllipse -> SkewedEllipseShape(
            color,
            strokeWidth,
        )
        Shape.Square -> SquareShape(
            color,
            strokeWidth,
        )
        Shape.Triangle -> TriangleShape(
            color,
            strokeWidth,
        )
    }
}