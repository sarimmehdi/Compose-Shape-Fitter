package com.sarim.composeshapefittersampleapp.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import com.sarim.composeshapefittersampleapp.R
import com.sarim.composeshapefittersampleapp.data.dto.shape.ShapeDto
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.domain.repository.ShapesRepository
import com.sarim.composeshapefittersampleapp.utils.MessageType
import com.sarim.composeshapefittersampleapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ShapesRepositoryImpl(
    private val dataStore: DataStore<ShapeDto>,
) : ShapesRepository {
    override val selectedShape: Flow<Resource<Shape>>
        get() =
            try {
                dataStore.data.map {
                    Log.i(
                        ShapesRepositoryImpl::class.java.simpleName,
                        "shapeDto = $it, selectedShapeType = ${it.selectedShapeType}",
                    )
                    Resource.Success(it.selectedShapeType)
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

    override suspend fun updateSelectedShape(selectedShape: Shape) =
        try {
            Log.i(
                ShapesRepositoryImpl::class.java.simpleName,
                "called updateSelectedShape with $selectedShape",
            )
            dataStore.updateData {
                it.copy(
                    selectedShapeType = selectedShape,
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
