package com.sarim.composeshapefittersampleapp.presentation

import com.sarim.composeshapefittersampleapp.domain.usecase.GetAllShapesUseCase
import com.sarim.composeshapefittersampleapp.domain.usecase.GetSelectedShapeUseCase
import com.sarim.composeshapefittersampleapp.domain.usecase.UpdateSelectedShapeUseCase

data class DrawingScreenUseCases(
    val getSelectedShapeUseCase: GetSelectedShapeUseCase,
    val getAllShapesUseCase: GetAllShapesUseCase,
    val updateSelectedShapeUseCase: UpdateSelectedShapeUseCase
)