package com.sarim.example_app_data.repository

import androidx.datastore.core.DataStore
import com.sarim.example_app_data.BuildConfig
import com.sarim.example_app_data.R
import com.sarim.example_app_data.dto.settings.SettingsDto
import com.sarim.example_app_data.dto.settings.toSettings
import com.sarim.example_app_domain.model.Settings
import com.sarim.example_app_domain.repository.SettingsRepository
import com.sarim.utils.LogType
import com.sarim.utils.MessageType
import com.sarim.utils.Resource
import com.sarim.utils.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<SettingsDto>,
) : SettingsRepository {
    override val settings: Flow<Resource<Settings>>
        get() =
            try {
                dataStore.data.map {
                    log(
                        tag = SettingsRepositoryImpl::class.java.simpleName,
                        messageBuilder = {
                            "settingsDto = $it, settings = ${it.toSettings()}"
                        },
                        logType = LogType.INFO,
                        shouldLog = BuildConfig.DEBUG,
                    )
                    Resource.Success(it.toSettings())
                }
            } catch (
                @Suppress("TooGenericExceptionCaught") e: Exception,
            ) {
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
            log(
                tag = SettingsRepositoryImpl::class.java.simpleName,
                messageBuilder = {
                    "called updateSettings with $settings"
                },
                logType = LogType.INFO,
                shouldLog = BuildConfig.DEBUG,
            )
            dataStore.updateData {
                it.copy(
                    showFingerTracedLines = settings.showFingerTracedLines,
                    showApproximatedShape = settings.showApproximatedShape,
                )
            }
            Resource.Success(true)
        } catch (
            @Suppress("TooGenericExceptionCaught") e: Exception,
        ) {
            Resource.Error(
                message =
                    e.localizedMessage?.let {
                        MessageType.StringMessage(it)
                    } ?: MessageType.IntMessage(R.string.unknown_reason_exception, e),
            )
        }
}
