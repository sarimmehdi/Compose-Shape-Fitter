package com.sarim.composeshapefittersampleapp.data.repository

import com.sarim.composeshapefittersampleapp.R
import com.sarim.composeshapefittersampleapp.data.dto.shape.ShapeDto
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.domain.repository.ShapesRepository
import com.sarim.composeshapefittersampleapp.utils.MessageType
import com.sarim.composeshapefittersampleapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ShapesRepositoryTestImpl : ShapesRepository {
    var shapeDto = ShapeDto()

    override val selectedShape: Flow<Resource<Shape>>
        get() =
            try {
                flowOf(Resource.Success(shapeDto.selectedShapeType))
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

    override suspend fun updateSelectedShape(selectedShape: Shape) =
        try {
            shapeDto = shapeDto.copy(selectedShapeType = selectedShape)
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
