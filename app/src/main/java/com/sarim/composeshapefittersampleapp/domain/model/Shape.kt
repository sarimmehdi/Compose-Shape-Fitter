package com.sarim.composeshapefittersampleapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Shape(
    val shapeType: ShapeType,
    val shapeStringId: Int,
) : Parcelable {

    companion object {
        enum class ShapeType {
            Circle,
            Ellipse,
            Hexagon,
            OrientedRectangle,
            OrientedSquare,
            Pentagon,
            Rectangle,
            OrientedEllipse,
            Square,
            Triangle
        }
    }
}