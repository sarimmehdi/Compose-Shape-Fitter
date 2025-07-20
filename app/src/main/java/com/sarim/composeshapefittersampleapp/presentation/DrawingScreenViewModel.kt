package com.sarim.composeshapefittersampleapp.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarim.composeshapefittersampleapp.R
import com.sarim.composeshapefittersampleapp.utils.MessageType
import com.sarim.composeshapefittersampleapp.utils.Resource
import com.sarim.composeshapefittersampleapp.utils.SnackBarController
import com.sarim.composeshapefittersampleapp.utils.SnackbarAction
import com.sarim.composeshapefittersampleapp.utils.SnackbarEvent
import com.sarim.composeshapefittersampleapp.utils.UiText
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DrawingScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val drawingScreenUseCases: DrawingScreenUseCases
) : ViewModel() {

    val state = savedStateHandle.getStateFlow(DRAWING_SCREEN_STATE_KEY, DrawingScreenState())

    init {
        viewModelScope.launch {
            drawingScreenUseCases.getAllShapesUseCase().collectLatest {
                when (it) {
                    is Resource.Error -> {
                        val message = it.message
                        SnackBarController.sendEvent(
                            event = SnackbarEvent(
                                message = when (message) {
                                    is MessageType.IntMessage -> UiText.StringResource(
                                        message.message,
                                        message.args
                                    )

                                    is MessageType.StringMessage -> UiText.StringResource(
                                        R.string.unable_to_get_selected_shape,
                                        message.message
                                    )
                                },
                                action = SnackbarAction(
                                    name = UiText.StringResource(R.string.error)
                                )
                            )
                        )
                    }
                    is Resource.Success -> {
                        savedStateHandle[DRAWING_SCREEN_STATE_KEY] = savedStateHandle
                            .get<DrawingScreenState>(DRAWING_SCREEN_STATE_KEY)?.copy(
                                allShapes = it.data.toImmutableList()
                            )
                    }
                }
            }
        }
        viewModelScope.launch {
            drawingScreenUseCases.getSelectedShapeUseCase().collectLatest {
                when (it) {
                    is Resource.Error -> {
                        val message = it.message
                        SnackBarController.sendEvent(
                            event = SnackbarEvent(
                                message = when (message) {
                                    is MessageType.IntMessage -> UiText.StringResource(
                                        message.message,
                                        message.args
                                    )

                                    is MessageType.StringMessage -> UiText.StringResource(
                                        R.string.unable_to_get_selected_shape,
                                        message.message
                                    )
                                },
                                action = SnackbarAction(
                                    name = UiText.StringResource(R.string.error)
                                )
                            )
                        )
                    }
                    is Resource.Success -> {
                        savedStateHandle[DRAWING_SCREEN_STATE_KEY] = savedStateHandle
                            .get<DrawingScreenState>(DRAWING_SCREEN_STATE_KEY)?.copy(
                                selectedShape = it.data
                            )
                    }
                }
            }
        }
    }

    fun onEvent(event: DrawingScreenToViewModelEvents) {
        when (event) {
            is DrawingScreenToViewModelEvents.SetSelectedShape -> {
                viewModelScope.launch {
                    drawingScreenUseCases.updateSelectedShapeUseCase(event.selectedShape)
                }
            }
            is DrawingScreenToViewModelEvents.SetApproximateShape -> {
                savedStateHandle[DRAWING_SCREEN_STATE_KEY] = savedStateHandle
                    .get<DrawingScreenState>(DRAWING_SCREEN_STATE_KEY)?.copy(
                        approximatedShape = event.approximatedShape
                    )
            }
            is DrawingScreenToViewModelEvents.SetDragging -> {
                savedStateHandle[DRAWING_SCREEN_STATE_KEY] = savedStateHandle
                    .get<DrawingScreenState>(DRAWING_SCREEN_STATE_KEY)?.copy(
                        isDragging = event.isDragging
                    )
            }
            is DrawingScreenToViewModelEvents.UpdateLines -> {
                savedStateHandle[DRAWING_SCREEN_STATE_KEY] = savedStateHandle
                    .get<DrawingScreenState>(DRAWING_SCREEN_STATE_KEY)?.copy(
                        lines = (state.value.lines + event.lines).toImmutableList()
                    )
            }
            is DrawingScreenToViewModelEvents.UpdatePoints -> {
                savedStateHandle[DRAWING_SCREEN_STATE_KEY] = savedStateHandle
                    .get<DrawingScreenState>(DRAWING_SCREEN_STATE_KEY)?.copy(
                        points = (state.value.points + event.points).toImmutableList()
                    )
                if (state.value.liveUpdateOfPoints) {
                    println("number of points: ${event.points.size}, values: ${event.points}")
                }
            }
            is DrawingScreenToViewModelEvents.SetLines -> {
                savedStateHandle[DRAWING_SCREEN_STATE_KEY] = savedStateHandle
                    .get<DrawingScreenState>(DRAWING_SCREEN_STATE_KEY)?.copy(
                        lines = event.lines
                    )
            }
            is DrawingScreenToViewModelEvents.SetPoints -> {
                savedStateHandle[DRAWING_SCREEN_STATE_KEY] = savedStateHandle
                    .get<DrawingScreenState>(DRAWING_SCREEN_STATE_KEY)?.copy(
                        points = event.points
                    )
                if (state.value.liveUpdateOfPoints) {
                    println("number of points: ${event.points.size}, values: ${event.points}")
                }
            }
        }
    }

    companion object {
        private const val DRAWING_SCREEN_STATE_KEY = "DRAWING_SCREEN_STATE_KEY"
    }
}