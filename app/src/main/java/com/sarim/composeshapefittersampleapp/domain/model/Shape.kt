package com.sarim.composeshapefittersampleapp.domain.model

import com.sarim.composeshapefittersampleapp.R

enum class Shape(val shapeStringId: Int) {
    Circle(R.string.circle),
    Ellipse(R.string.ellipse),
    Hexagon(R.string.hexagon),
    OrientedRectangle(R.string.oriented_rectangle),
    OrientedSquare(R.string.oriented_square),
    Pentagon(R.string.pentagon),
    Rectangle(R.string.rectangle),
    OrientedEllipse(R.string.oriented_ellipse),
    Square(R.string.square),
    Triangle(R.string.triangle)
}