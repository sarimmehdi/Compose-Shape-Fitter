package com.sarim.composeshapefittersampleapp.presentation

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
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
import kotlinx.collections.immutable.toImmutableList
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith

typealias ParcelableOffsetPair = Pair<
    @WriteWith<OffsetParceler> Offset,
    @WriteWith<OffsetParceler> Offset,
>

@Parcelize
data class DrawingScreenState(
    val selectedShape: Shape = Shape.Circle,
    val allShapes: ImmutableList<Shape> = Shape.entries.toImmutableList(),
    val isDragging: Boolean = false,
    val approximatedShape: ApproximatedShape? = null,
    val points: ImmutableList<@WriteWith<OffsetParceler> Offset> = persistentListOf(),
    val lines: ImmutableList<ParcelableOffsetPair> = persistentListOf(),
    val showFingerTracedLines: Boolean = true,
    val showApproximatedShape: Boolean = true,
    val showSettingsDropDown: Boolean = false,
) : Parcelable {
    @Composable
    fun getDrawableShape(
        color: Color,
        strokeWidth: Float,
    ) = when (selectedShape) {
        Shape.Circle ->
            CircleShape(
                color,
                strokeWidth,
                inPreviewMode = LocalInspectionMode.current
            )
        Shape.Ellipse ->
            EllipseShape(
                color,
                strokeWidth,
                inPreviewMode = LocalInspectionMode.current
            )
        Shape.Hexagon ->
            HexagonShape(
                color,
                strokeWidth,
                inPreviewMode = LocalInspectionMode.current
            )
        Shape.OrientedRectangle ->
            ObbShape(
                color,
                strokeWidth,
                false,
                inPreviewMode = LocalInspectionMode.current
            )
        Shape.OrientedSquare ->
            ObbShape(
                color,
                strokeWidth,
                true,
                inPreviewMode = LocalInspectionMode.current
            )
        Shape.Pentagon ->
            PentagonShape(
                color,
                strokeWidth,
                inPreviewMode = LocalInspectionMode.current
            )
        Shape.Rectangle ->
            RectangleShape(
                color,
                strokeWidth,
                inPreviewMode = LocalInspectionMode.current
            )
        Shape.OrientedEllipse ->
            SkewedEllipseShape(
                color,
                strokeWidth,
                inPreviewMode = LocalInspectionMode.current
            )
        Shape.Square ->
            SquareShape(
                color,
                strokeWidth,
                inPreviewMode = LocalInspectionMode.current
            )
        Shape.Triangle ->
            TriangleShape(
                color,
                strokeWidth,
                inPreviewMode = LocalInspectionMode.current
            )
    }
}
