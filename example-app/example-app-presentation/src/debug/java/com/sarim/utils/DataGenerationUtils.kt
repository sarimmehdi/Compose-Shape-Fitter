package com.sarim.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.sarim.compose_shape_fitter.shape.CircleShape
import com.sarim.compose_shape_fitter.shape.DrawableShape
import com.sarim.compose_shape_fitter.shape.EllipseShape
import com.sarim.compose_shape_fitter.shape.HexagonShape
import com.sarim.compose_shape_fitter.shape.ObbShape
import com.sarim.compose_shape_fitter.shape.PentagonShape
import com.sarim.compose_shape_fitter.shape.RectangleShape
import com.sarim.compose_shape_fitter.shape.SkewedEllipseShape
import com.sarim.compose_shape_fitter.shape.SquareShape
import com.sarim.compose_shape_fitter.shape.TriangleShape
import com.sarim.example_app_domain.model.Shape
import com.sarim.example_app_presentation.DEFAULT_STROKE_WIDTH

internal fun getDrawableShapeFromShape(shape: Shape) =
    when (shape) {
        Shape.Circle -> CircleShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true)
        Shape.Ellipse -> EllipseShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true)
        Shape.Hexagon -> HexagonShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true)
        Shape.OrientedRectangle ->
            ObbShape(
                Color.Blue,
                DEFAULT_STROKE_WIDTH,
                allSidesEqual = true,
                inPreviewMode = true,
            )
        Shape.OrientedSquare ->
            ObbShape(
                Color.Blue,
                DEFAULT_STROKE_WIDTH,
                allSidesEqual = false,
                inPreviewMode = true,
            )
        Shape.Pentagon -> PentagonShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true)
        Shape.Rectangle -> RectangleShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true)
        Shape.OrientedEllipse -> SkewedEllipseShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true)
        Shape.Square -> SquareShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true)
        Shape.Triangle -> TriangleShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true)
    }

internal fun generateDummyPoints(drawableShape: DrawableShape): List<Offset> {
    val commonStartX = 400f
    val commonStartY = 1000f
    val defaultSize = 250f
    return when (drawableShape) {
        is CircleShape ->
            generateEllipsePoints(
                commonStartX + defaultSize,
                commonStartY + defaultSize,
                defaultSize,
                defaultSize,
            )
        is EllipseShape ->
            generateEllipsePoints(
                commonStartX + defaultSize,
                commonStartY + defaultSize,
                defaultSize * 1.5f,
                defaultSize,
            )
        is HexagonShape ->
            generatePolygonPoints(
                6,
                commonStartX,
                commonStartY,
            )
        is ObbShape ->
            if (drawableShape.allSidesEqual) {
                listOf(
                    Offset(commonStartX, commonStartY),
                    Offset(commonStartX + defaultSize, commonStartY + defaultSize / 2),
                    Offset(commonStartX, commonStartY + defaultSize * 1.5f),
                    Offset(commonStartX - defaultSize, commonStartY + defaultSize),
                    Offset(commonStartX, commonStartY),
                )
            } else {
                listOf(
                    Offset(commonStartX, commonStartY),
                    Offset(commonStartX + defaultSize * 1.5f, commonStartY + defaultSize / 3),
                    Offset(commonStartX + defaultSize * 0.5f, commonStartY + defaultSize * 1.8f),
                    Offset(commonStartX - defaultSize, commonStartY + defaultSize),
                    Offset(commonStartX, commonStartY),
                )
            }
        is PentagonShape ->
            generatePolygonPoints(
                5,
                commonStartX,
                commonStartY,
            )
        is RectangleShape ->
            listOf(
                Offset(commonStartX, commonStartY),
                Offset(commonStartX + defaultSize * 2, commonStartY),
                Offset(commonStartX + defaultSize * 2, commonStartY + defaultSize),
                Offset(commonStartX, commonStartY + defaultSize),
                Offset(commonStartX, commonStartY),
            )
        is SkewedEllipseShape ->
            generateEllipsePoints(
                commonStartX + defaultSize,
                commonStartY + defaultSize,
                defaultSize,
                defaultSize * 0.7f,
                20,
            ).map { it.copy(x = it.x + (it.y - commonStartY - defaultSize) * 0.3f) }
        is SquareShape ->
            listOf(
                Offset(commonStartX, commonStartY),
                Offset(commonStartX + defaultSize, commonStartY),
                Offset(commonStartX + defaultSize, commonStartY + defaultSize),
                Offset(commonStartX, commonStartY + defaultSize),
                Offset(commonStartX, commonStartY),
            )
        is TriangleShape ->
            listOf(
                Offset(commonStartX, commonStartY + defaultSize),
                Offset(commonStartX + defaultSize / 2, commonStartY),
                Offset(commonStartX + defaultSize, commonStartY + defaultSize),
                Offset(commonStartX, commonStartY + defaultSize),
            )
    }
}
