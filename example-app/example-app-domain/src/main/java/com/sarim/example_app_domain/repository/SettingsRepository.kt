package com.sarim.example_app_domain.repository

import com.sarim.example_app_domain.model.Settings
import com.sarim.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<Resource<Settings>>

    suspend fun updateSettings(settings: Settings): Resource<Boolean>
}
