package com.sarim.example_app_data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.sarim.example_app_data.BuildConfig
import com.sarim.example_app_data.R
import com.sarim.example_app_data.dto.shape.ShapeDto
import com.sarim.example_app_domain.model.Shape
import com.sarim.example_app_domain.repository.ShapesRepository
import com.sarim.utils.LogType
import com.sarim.utils.MessageType
import com.sarim.utils.Resource
import com.sarim.utils.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ShapesRepositoryImpl(
    private val dataStore: DataStore<ShapeDto>,
    private val dataStoreName: String,
) : ShapesRepository {
    override val selectedShape: Flow<Resource<Shape>>
        get() = dataStore.data.map {
            log(
                tag = ShapesRepositoryImpl::class.java.simpleName,
                messageBuilder = {
                    "shapeDto = $it, selectedShapeType = ${it.selectedShapeType}"
                },
                logType = LogType.INFO,
                shouldLog = BuildConfig.DEBUG,
            )
            Resource.Success(it.selectedShapeType) as Resource<Shape>
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

    override suspend fun updateSelectedShape(selectedShape: Shape) =
        try {
            log(
                tag = ShapesRepositoryImpl::class.java.simpleName,
                messageBuilder = {
                    "called updateSelectedShape with $selectedShape"
                },
                logType = LogType.INFO,
                shouldLog = BuildConfig.DEBUG,
            )
            dataStore.updateData {
                it.copy(
                    selectedShapeType = selectedShape,
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
