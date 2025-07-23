package com.sarim.composeshapefittersampleapp.data.dto.settings

import com.sarim.composeshapefittersampleapp.domain.model.Settings
import kotlinx.serialization.Serializable

@Serializable
data class SettingsDto(
    val showFingerTracedLines: Boolean = true,
    val showApproximatedShape: Boolean = true,
)

fun SettingsDto.toSettings() = Settings(
    showFingerTracedLines = showFingerTracedLines,
    showApproximatedShape = showApproximatedShape,
)