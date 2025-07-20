package com.sarim.composeshapefittersampleapp.domain.usecase

import com.sarim.composeshapefittersampleapp.domain.repository.SettingsRepository

class GetSettingsUseCase(
    val repository: SettingsRepository
) {

    operator fun invoke() = repository.settings
}