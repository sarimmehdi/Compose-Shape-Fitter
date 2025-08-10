package com.sarim.example_app_domain.usecase

import com.sarim.example_app_domain.model.Shape
import com.sarim.example_app_domain.repository.ShapesRepository

class UpdateSelectedShapeUseCase(
    val repository: ShapesRepository,
) {
    suspend operator fun invoke(selectedShape: Shape) = repository.updateSelectedShape(selectedShape)
}
