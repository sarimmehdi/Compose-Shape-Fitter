package com.sarim.example_app_presentation

import com.sarim.example_app_domain.usecase.GetSelectedShapeUseCase
import com.sarim.example_app_domain.usecase.GetSettingsUseCase
import com.sarim.example_app_domain.usecase.UpdateSelectedShapeUseCase
import com.sarim.example_app_domain.usecase.UpdateSettingsUseCase

data class DrawingScreenUseCases(
    val getSettingsUseCase: GetSettingsUseCase,
    val getSelectedShapeUseCase: GetSelectedShapeUseCase,
    val updateSelectedShapeUseCase: UpdateSelectedShapeUseCase,
    val updateSettingsUseCase: UpdateSettingsUseCase,
)
