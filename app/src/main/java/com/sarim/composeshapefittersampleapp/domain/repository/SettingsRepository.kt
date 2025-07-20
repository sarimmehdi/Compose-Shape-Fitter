package com.sarim.composeshapefittersampleapp.domain.repository

import com.sarim.composeshapefittersampleapp.domain.model.Settings
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<Resource<Settings>>

    suspend fun updateSettings(settings: Settings): Resource<Boolean>
}