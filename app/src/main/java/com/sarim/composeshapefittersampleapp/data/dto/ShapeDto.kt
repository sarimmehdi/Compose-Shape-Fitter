package com.sarim.composeshapefittersampleapp.data.dto

import com.sarim.composeshapefittersampleapp.R
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class ShapeDto(
    val selectedShapeType: Shape.Companion.ShapeType = Shape.Companion.ShapeType.Circle,
    val allShapes: PersistentList<Shape.Companion.ShapeType> = persistentListOf(
        Shape.Companion.ShapeType.Circle,
        Shape.Companion.ShapeType.Ellipse,
        Shape.Companion.ShapeType.Hexagon,
        Shape.Companion.ShapeType.OrientedRectangle,
        Shape.Companion.ShapeType.OrientedSquare,
        Shape.Companion.ShapeType.Pentagon,
        Shape.Companion.ShapeType.Rectangle,
        Shape.Companion.ShapeType.OrientedEllipse,
        Shape.Companion.ShapeType.Square,
        Shape.Companion.ShapeType.Triangle,
    )
)

fun ShapeDto.toSelectedShape(selectedShapeStringId: Int) = Shape(
    shapeType = selectedShapeType,
    shapeStringId = selectedShapeStringId,
)

fun ShapeDto.toShapes() = allShapes.map {
    Shape(
        shapeType = it,
        shapeStringId = when (it) {
            Shape.Companion.ShapeType.Circle -> R.string.circle
            Shape.Companion.ShapeType.Ellipse -> R.string.ellipse
            Shape.Companion.ShapeType.Hexagon -> R.string.hexagon
            Shape.Companion.ShapeType.OrientedRectangle -> R.string.oriented_rectangle
            Shape.Companion.ShapeType.OrientedSquare -> R.string.oriented_square
            Shape.Companion.ShapeType.Pentagon -> R.string.pentagon
            Shape.Companion.ShapeType.Rectangle -> R.string.rectangle
            Shape.Companion.ShapeType.OrientedEllipse -> R.string.oriented_ellipse
            Shape.Companion.ShapeType.Square -> R.string.square
            Shape.Companion.ShapeType.Triangle -> R.string.triangle
        }
    )
}