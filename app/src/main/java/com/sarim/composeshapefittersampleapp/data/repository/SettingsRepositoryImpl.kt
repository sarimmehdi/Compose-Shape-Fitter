package com.sarim.composeshapefittersampleapp.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import com.sarim.composeshapefittersampleapp.R
import com.sarim.composeshapefittersampleapp.data.dto.settings.SettingsDto
import com.sarim.composeshapefittersampleapp.data.dto.settings.toSettings
import com.sarim.composeshapefittersampleapp.domain.model.Settings
import com.sarim.composeshapefittersampleapp.domain.repository.SettingsRepository
import com.sarim.composeshapefittersampleapp.utils.MessageType
import com.sarim.composeshapefittersampleapp.utils.Resource
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
                    Log.i(
                        SettingsRepositoryImpl::class.java.simpleName,
                        "settingsDto = $it, settings = ${it.toSettings()}",
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
            Log.i(
                SettingsRepositoryImpl::class.java.simpleName,
                "called updateSettings with $settings",
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
