package com.sarim.composeshapefittersampleapp.domain.usecase

import com.sarim.composeshapefittersampleapp.domain.model.Settings
import com.sarim.composeshapefittersampleapp.domain.repository.SettingsRepository

class UpdateSettingsUseCase(
    val repository: SettingsRepository
) {

    suspend operator fun invoke(settings: Settings) = repository.updateSettings(settings)
}