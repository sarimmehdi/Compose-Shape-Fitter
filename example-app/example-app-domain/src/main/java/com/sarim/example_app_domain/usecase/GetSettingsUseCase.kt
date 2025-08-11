package com.sarim.example_app_domain.usecase

import com.sarim.example_app_domain.repository.SettingsRepository

class GetSettingsUseCase(
    val repository: SettingsRepository,
) {
    operator fun invoke() = repository.settings
}
