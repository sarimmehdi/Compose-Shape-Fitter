package com.sarim.example_app_domain.repository

import com.sarim.example_app_domain.model.Shape
import com.sarim.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ShapesRepository {
    val selectedShape: Flow<Resource<Shape>>

    suspend fun updateSelectedShape(selectedShape: Shape): Resource<Boolean>
}
