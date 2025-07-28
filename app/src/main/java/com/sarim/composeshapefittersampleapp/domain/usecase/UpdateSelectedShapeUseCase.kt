package com.sarim.composeshapefittersampleapp.domain.usecase

import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.domain.repository.ShapesRepository

class UpdateSelectedShapeUseCase(
    val repository: ShapesRepository,
) {
    suspend operator fun invoke(selectedShape: Shape) = repository.updateSelectedShape(selectedShape)
}
