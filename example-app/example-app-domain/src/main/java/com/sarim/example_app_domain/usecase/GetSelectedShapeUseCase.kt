package com.sarim.example_app_domain.usecase

import com.sarim.example_app_domain.repository.ShapesRepository


class GetSelectedShapeUseCase(
    val repository: ShapesRepository,
) {
    operator fun invoke() = repository.selectedShape
}
