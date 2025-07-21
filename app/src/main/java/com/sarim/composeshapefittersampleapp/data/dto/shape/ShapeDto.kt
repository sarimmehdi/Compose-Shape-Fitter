package com.sarim.composeshapefittersampleapp.data.dto.shape

import com.sarim.composeshapefittersampleapp.domain.model.Shape
import kotlinx.serialization.Serializable

@Serializable
data class ShapeDto(
    val selectedShapeType: Shape = Shape.Circle,
)