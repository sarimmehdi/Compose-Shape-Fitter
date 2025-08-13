package com.sarim.example_app_data.dto.settings

import com.sarim.example_app_domain.model.Settings
import kotlinx.serialization.Serializable

@Serializable
data class SettingsDto(
    val showFingerTracedLines: Boolean = true,
    val showApproximatedShape: Boolean = true,
)

internal fun SettingsDto.toSettings() =
    Settings(
        showFingerTracedLines = showFingerTracedLines,
        showApproximatedShape = showApproximatedShape,
    )
