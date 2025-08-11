package com.sarim.example_app_presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarim.compose_shape_fitter.BuildConfig
import com.sarim.example_app_domain.model.Settings
import com.sarim.utils.DispatcherProvider
import com.sarim.utils.LogType
import com.sarim.utils.MessageType
import com.sarim.utils.Resource
import com.sarim.utils.SnackBarController
import com.sarim.utils.SnackbarAction
import com.sarim.utils.SnackbarEvent
import com.sarim.utils.UiText
import com.sarim.utils.log
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DrawingScreenViewModel(
    private val dispatchers: DispatcherProvider,
    private val savedStateHandle: SavedStateHandle,
    private val drawingScreenUseCases: DrawingScreenUseCases,
) : ViewModel() {
    val state = savedStateHandle.getStateFlow(DRAWING_SCREEN_STATE_KEY, DrawingScreenState())

    init {
        viewModelScope.launch(dispatchers.main) {
            drawingScreenUseCases.getSettingsUseCase().collectLatest {
                log(
                    tag = DrawingScreenViewModel::class.java.simpleName,
                    messageBuilder = {
                        "getSettingsUseCase() returned $it"
                    },
                    logType = LogType.DEBUG,
                    shouldLog = BuildConfig.DEBUG,
                )
                when (it) {
                    is Resource.Error -> {
                        val message = it.message
                        SnackBarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    message =
                                        when (message) {
                                            is MessageType.IntMessage ->
                                                @Suppress("SpreadOperator")
                                                UiText.StringResource(
                                                    message.message,
                                                    *message.args,
                                                )

                                            is MessageType.StringMessage ->
                                                UiText.StringResource(
                                                    R.string.unable_to_get_settings,
                                                    message.message,
                                                )
                                        },
                                    action =
                                        SnackbarAction(
                                            name = UiText.StringResource(R.string.error),
                                        ),
                                ),
                        )
                    }
                    is Resource.Success -> {
                        val currState = (savedStateHandle[DRAWING_SCREEN_STATE_KEY] as DrawingScreenState?)
                        savedStateHandle[DRAWING_SCREEN_STATE_KEY] =
                            currState?.copy(
                                showFingerTracedLines = it.data.showFingerTracedLines,
                                showApproximatedShape = it.data.showApproximatedShape,
                            )
                    }
                }
            }
        }
        viewModelScope.launch(dispatchers.main) {
            drawingScreenUseCases.getSelectedShapeUseCase().collectLatest {
                log(
                    tag = DrawingScreenViewModel::class.java.simpleName,
                    messageBuilder = {
                        "getSelectedShapeUseCase() returned $it"
                    },
                    logType = LogType.DEBUG,
                    shouldLog = BuildConfig.DEBUG,
                )
                when (it) {
                    is Resource.Error -> {
                        val message = it.message
                        SnackBarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    message =
                                        when (message) {
                                            is MessageType.IntMessage ->
                                                UiText.StringResource(
                                                    message.message,
                                                    message.args,
                                                )

                                            is MessageType.StringMessage ->
                                                UiText.StringResource(
                                                    R.string.unable_to_get_selected_shape,
                                                    message.message,
                                                )
                                        },
                                    action =
                                        SnackbarAction(
                                            name = UiText.StringResource(R.string.error),
                                        ),
                                ),
                        )
                    }
                    is Resource.Success -> {
                        val currState = (savedStateHandle[DRAWING_SCREEN_STATE_KEY] as DrawingScreenState?)
                        savedStateHandle[DRAWING_SCREEN_STATE_KEY] =
                            currState?.copy(
                                selectedShape = it.data,
                            )
                    }
                }
            }
        }
    }

    @Suppress("LongMethod")
    fun onEvent(event: DrawingScreenToViewModelEvents) {
        when (event) {
            is DrawingScreenToViewModelEvents.SetSelectedShape -> {
                viewModelScope.launch(dispatchers.main) {
                    drawingScreenUseCases.updateSelectedShapeUseCase(event.selectedShape)
                }
            }
            is DrawingScreenToViewModelEvents.SetApproximateShape -> {
                val currState = (savedStateHandle[DRAWING_SCREEN_STATE_KEY] as DrawingScreenState?)
                savedStateHandle[DRAWING_SCREEN_STATE_KEY] =
                    currState?.copy(
                        approximatedShape = event.approximatedShape,
                    )
            }
            is DrawingScreenToViewModelEvents.SetDragging -> {
                val currState = (savedStateHandle[DRAWING_SCREEN_STATE_KEY] as DrawingScreenState?)
                savedStateHandle[DRAWING_SCREEN_STATE_KEY] =
                    currState?.copy(
                        isDragging = event.isDragging,
                    )
            }
            is DrawingScreenToViewModelEvents.UpdateLines -> {
                val currState = (savedStateHandle[DRAWING_SCREEN_STATE_KEY] as DrawingScreenState?)
                savedStateHandle[DRAWING_SCREEN_STATE_KEY] =
                    currState?.copy(
                        lines = (state.value.lines + event.line).toImmutableList(),
                    )
            }
            is DrawingScreenToViewModelEvents.UpdatePoints -> {
                val currState = (savedStateHandle[DRAWING_SCREEN_STATE_KEY] as DrawingScreenState?)
                savedStateHandle[DRAWING_SCREEN_STATE_KEY] =
                    currState?.copy(
                        points = (state.value.points + event.point).toImmutableList(),
                    )
            }
            is DrawingScreenToViewModelEvents.SetLines -> {
                val currState = (savedStateHandle[DRAWING_SCREEN_STATE_KEY] as DrawingScreenState?)
                savedStateHandle[DRAWING_SCREEN_STATE_KEY] =
                    currState?.copy(
                        lines = event.lines,
                    )
            }
            is DrawingScreenToViewModelEvents.SetPoints -> {
                val currState = (savedStateHandle[DRAWING_SCREEN_STATE_KEY] as DrawingScreenState?)
                savedStateHandle[DRAWING_SCREEN_STATE_KEY] =
                    currState?.copy(
                        points = event.points,
                    )
            }
            is DrawingScreenToViewModelEvents.ToggleSettings -> {
                viewModelScope.launch(dispatchers.main) {
                    drawingScreenUseCases.updateSettingsUseCase(
                        Settings(
                            showFingerTracedLines = event.showFingerTracedLines,
                            showApproximatedShape = event.showApproximatedShape,
                        ),
                    )
                }
            }
            is DrawingScreenToViewModelEvents.ToggleSettingsDropDown -> {
                val currState = (savedStateHandle[DRAWING_SCREEN_STATE_KEY] as DrawingScreenState?)
                savedStateHandle[DRAWING_SCREEN_STATE_KEY] =
                    currState?.copy(
                        showSettingsDropDown = !state.value.showSettingsDropDown,
                    )
            }
        }
    }

    companion object {
        const val DRAWING_SCREEN_STATE_KEY = "DRAWING_SCREEN_STATE_KEY"
    }
}
