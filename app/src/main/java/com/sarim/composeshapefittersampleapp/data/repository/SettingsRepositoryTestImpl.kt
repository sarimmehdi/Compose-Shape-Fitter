package com.sarim.composeshapefittersampleapp.data.repository

import com.sarim.composeshapefittersampleapp.R
import com.sarim.composeshapefittersampleapp.data.dto.settings.SettingsDto
import com.sarim.composeshapefittersampleapp.data.dto.settings.toSettings
import com.sarim.composeshapefittersampleapp.domain.model.Settings
import com.sarim.composeshapefittersampleapp.domain.repository.SettingsRepository
import com.sarim.composeshapefittersampleapp.utils.MessageType
import com.sarim.composeshapefittersampleapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SettingsRepositoryTestImpl : SettingsRepository {
    var settingsDto = SettingsDto()

    override val settings: Flow<Resource<Settings>>
        get() =
            try {
                flowOf(Resource.Success(settingsDto.toSettings()))
            } catch (e: Exception) {
                flowOf(
                    Resource.Error(
                        message =
                            e.localizedMessage?.let {
                                MessageType.StringMessage(it)
                            } ?: MessageType.IntMessage(R.string.unknown_reason_exception, e),
                    ),
                )
            }

    override suspend fun updateSettings(settings: Settings) =
        try {
            settingsDto = settingsDto.copy(
                showFingerTracedLines = settings.showFingerTracedLines,
                showApproximatedShape = settings.showApproximatedShape,
            )
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(
                message =
                    e.localizedMessage?.let {
                        MessageType.StringMessage(it)
                    } ?: MessageType.IntMessage(R.string.unknown_reason_exception, e),
            )
        }
}
