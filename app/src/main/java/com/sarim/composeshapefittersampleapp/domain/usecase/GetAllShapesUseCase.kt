package com.sarim.composeshapefittersampleapp.domain.usecase

import com.sarim.composeshapefittersampleapp.domain.model.Shape

class GetAllShapesUseCase {
    operator fun invoke() = Shape.entries
}
