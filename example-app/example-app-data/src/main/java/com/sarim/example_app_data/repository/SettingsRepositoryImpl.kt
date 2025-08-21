package com.sarim.example_app_data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.sarim.example_app_data.BuildConfig
import com.sarim.example_app_data.R
import com.sarim.example_app_data.dto.settings.SettingsDto
import com.sarim.example_app_data.dto.settings.toSettings
import com.sarim.example_app_domain.model.Settings
import com.sarim.example_app_domain.repository.SettingsRepository
import com.sarim.utils.log.LogType
import com.sarim.utils.ui.MessageType
import com.sarim.utils.ui.Resource
import com.sarim.utils.log.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<SettingsDto>,
    private val dataStoreName: String,
) : SettingsRepository {
    override val settings: Flow<Resource<Settings>>
        get() = dataStore.data.map {
            log(
                tag = SettingsRepositoryImpl::class.java.simpleName,
                messageBuilder = {
                    "settingsDto = $it, settings = ${it.toSettings()}"
                },
                logType = LogType.INFO,
                shouldLog = BuildConfig.DEBUG,
            )
            Resource.Success(it.toSettings()) as Resource<Settings>
        }.catch { e ->
            emit(
                Resource.Error(
                    message =
                        e.localizedMessage?.let {
                            MessageType.StringMessage(it)
                        } ?: MessageType.IntMessage(
                            R.string.unknown_reason_read_exception,
                            dataStoreName, e
                        ),
                )
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
        } catch (e: IOException) {
            Resource.Error(
                message =
                    e.localizedMessage?.let {
                        MessageType.StringMessage(it)
                    } ?: MessageType.IntMessage(
                        R.string.unknown_reason_write_exception,
                        dataStoreName, e
                    ),
            )
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
