package com.sarim.example_app_presentation

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
import com.sarim.example_app_domain.model.Shape
import com.sarim.utils.OffsetParceler
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
    val inPreviewMode: Boolean = false,
) : Parcelable {
    
    fun getDrawableShape(
        color: Color,
        strokeWidth: Float,
    ) = when (selectedShape) {
        Shape.Circle ->
            CircleShape(
                color,
                strokeWidth,
                inPreviewMode = inPreviewMode
            )
        Shape.Ellipse ->
            EllipseShape(
                color,
                strokeWidth,
                inPreviewMode = inPreviewMode
            )
        Shape.Hexagon ->
            HexagonShape(
                color,
                strokeWidth,
                inPreviewMode = inPreviewMode
            )
        Shape.OrientedRectangle ->
            ObbShape(
                color,
                strokeWidth,
                false,
                inPreviewMode = inPreviewMode
            )
        Shape.OrientedSquare ->
            ObbShape(
                color,
                strokeWidth,
                true,
                inPreviewMode = inPreviewMode
            )
        Shape.Pentagon ->
            PentagonShape(
                color,
                strokeWidth,
                inPreviewMode = inPreviewMode
            )
        Shape.Rectangle ->
            RectangleShape(
                color,
                strokeWidth,
                inPreviewMode = inPreviewMode
            )
        Shape.OrientedEllipse ->
            SkewedEllipseShape(
                color,
                strokeWidth,
                inPreviewMode = inPreviewMode
            )
        Shape.Square ->
            SquareShape(
                color,
                strokeWidth,
                inPreviewMode = inPreviewMode
            )
        Shape.Triangle ->
            TriangleShape(
                color,
                strokeWidth,
                inPreviewMode = inPreviewMode
            )
    }
}
