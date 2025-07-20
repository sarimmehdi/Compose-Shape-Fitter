package com.sarim.composeshapefittersampleapp.data.repository

import androidx.datastore.core.DataStore
import com.sarim.composeshapefittersampleapp.R
import com.sarim.composeshapefittersampleapp.data.dto.ShapeDto
import com.sarim.composeshapefittersampleapp.data.dto.toSelectedShape
import com.sarim.composeshapefittersampleapp.data.dto.toShapes
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.domain.repository.ShapesRepository
import com.sarim.composeshapefittersampleapp.utils.MessageType
import com.sarim.composeshapefittersampleapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ShapesRepositoryImpl(
    private val dataStore: DataStore<ShapeDto>
) : ShapesRepository {

    override val selectedShape: Flow<Resource<Shape>>
        get() = try {
            dataStore.data.map {
                Resource.Success(
                    when (it.selectedShapeType) {
                        Shape.Companion.ShapeType.Circle -> it.toSelectedShape(R.string.circle)
                        Shape.Companion.ShapeType.Ellipse -> it.toSelectedShape(R.string.ellipse)
                        Shape.Companion.ShapeType.Hexagon -> it.toSelectedShape(R.string.hexagon)
                        Shape.Companion.ShapeType.OrientedRectangle -> it.toSelectedShape(R.string.oriented_rectangle)
                        Shape.Companion.ShapeType.OrientedSquare -> it.toSelectedShape(R.string.oriented_square)
                        Shape.Companion.ShapeType.Pentagon -> it.toSelectedShape(R.string.pentagon)
                        Shape.Companion.ShapeType.Rectangle -> it.toSelectedShape(R.string.rectangle)
                        Shape.Companion.ShapeType.OrientedEllipse -> it.toSelectedShape(R.string.oriented_ellipse)
                        Shape.Companion.ShapeType.Square -> it.toSelectedShape(R.string.square)
                        Shape.Companion.ShapeType.Triangle -> it.toSelectedShape(R.string.triangle)
                    }
                )
            }
        } catch (e: Exception) {
            flowOf(
                Resource.Error(
                    message = e.localizedMessage?.let {
                        MessageType.StringMessage(it)
                    } ?: MessageType.IntMessage(R.string.unknown_reason_exception, e)
                )
            )
        }

    override val allShapes: Flow<Resource<List<Shape>>>
        get() = try {
            dataStore.data.map {
                Resource.Success(it.toShapes())
            }
        } catch (e: Exception) {
            flowOf(
                Resource.Error(
                    message = e.localizedMessage?.let {
                        MessageType.StringMessage(it)
                    } ?: MessageType.IntMessage(R.string.unknown_reason_exception, e)
                )
            )
        }

    override suspend fun updateSelectedShape(selectedShape: Shape) = try {
        dataStore.updateData {
            it.copy(
                selectedShapeType = selectedShape.shapeType
            )
        }
        Resource.Success(true)
    } catch (e: Exception) {
        Resource.Error(
            message = e.localizedMessage?.let {
                MessageType.StringMessage(it)
            } ?: MessageType.IntMessage(R.string.unknown_reason_exception, e)
        )
    }
}