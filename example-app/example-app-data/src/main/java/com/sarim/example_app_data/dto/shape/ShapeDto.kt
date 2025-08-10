package com.sarim.example_app_data.dto.shape

import com.sarim.example_app_domain.model.Shape
import kotlinx.serialization.Serializable

@Serializable
data class ShapeDto(
    val selectedShapeType: Shape = Shape.Circle,
)
