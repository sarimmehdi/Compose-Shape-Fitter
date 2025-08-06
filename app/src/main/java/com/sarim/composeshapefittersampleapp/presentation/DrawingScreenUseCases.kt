package com.sarim.composeshapefittersampleapp.presentation

import com.sarim.composeshapefittersampleapp.domain.usecase.GetSelectedShapeUseCase
import com.sarim.composeshapefittersampleapp.domain.usecase.GetSettingsUseCase
import com.sarim.composeshapefittersampleapp.domain.usecase.UpdateSelectedShapeUseCase
import com.sarim.composeshapefittersampleapp.domain.usecase.UpdateSettingsUseCase

data class DrawingScreenUseCases(
    val getSettingsUseCase: GetSettingsUseCase,
    val getSelectedShapeUseCase: GetSelectedShapeUseCase,
    val updateSelectedShapeUseCase: UpdateSelectedShapeUseCase,
    val updateSettingsUseCase: UpdateSettingsUseCase,
)
