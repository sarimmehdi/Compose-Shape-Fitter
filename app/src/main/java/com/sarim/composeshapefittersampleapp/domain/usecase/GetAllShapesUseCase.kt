package com.sarim.composeshapefittersampleapp.domain.usecase

import com.sarim.composeshapefittersampleapp.domain.repository.ShapesRepository

class GetAllShapesUseCase(
    val repository: ShapesRepository
) {

    operator fun invoke() = repository.allShapes
}