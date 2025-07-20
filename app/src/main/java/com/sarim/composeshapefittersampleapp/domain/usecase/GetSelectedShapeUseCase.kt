package com.sarim.composeshapefittersampleapp.domain.usecase

import com.sarim.composeshapefittersampleapp.domain.repository.ShapesRepository

class GetSelectedShapeUseCase(
    val repository: ShapesRepository
) {

    operator fun invoke() = repository.selectedShape
}