package com.sarim.composeshapefittersampleapp.domain.repository

import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ShapesRepository {

    val selectedShape: Flow<Resource<Shape>>

    suspend fun updateSelectedShape(selectedShape: Shape): Resource<Boolean>
}