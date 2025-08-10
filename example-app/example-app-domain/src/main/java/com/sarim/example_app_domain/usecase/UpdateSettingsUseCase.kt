package com.sarim.example_app_domain.usecase

import com.sarim.example_app_domain.model.Settings
import com.sarim.example_app_domain.repository.SettingsRepository

class UpdateSettingsUseCase(
    val repository: SettingsRepository,
) {
    suspend operator fun invoke(settings: Settings) = repository.updateSettings(settings)
}
