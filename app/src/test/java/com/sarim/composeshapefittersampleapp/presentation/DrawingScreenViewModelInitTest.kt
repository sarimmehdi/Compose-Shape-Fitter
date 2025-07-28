package com.sarim.composeshapefittersampleapp.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.sarim.composeshapefittersampleapp.R
import com.sarim.composeshapefittersampleapp.TestDispatchers
import com.sarim.composeshapefittersampleapp.domain.model.Settings
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenViewModel.Companion.DRAWING_SCREEN_STATE_KEY
import com.sarim.composeshapefittersampleapp.utils.MessageType
import com.sarim.composeshapefittersampleapp.utils.Resource
import com.sarim.composeshapefittersampleapp.utils.SnackBarController
import com.sarim.composeshapefittersampleapp.utils.SnackbarAction
import com.sarim.composeshapefittersampleapp.utils.SnackbarEvent
import com.sarim.composeshapefittersampleapp.utils.UiText
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.flatMap
import io.kotest.property.exhaustive.map
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DrawingScreenViewModelInitTest {
    lateinit var savedStateHandle: SavedStateHandle
    lateinit var viewModel: DrawingScreenViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        mockkObject(SnackBarController)
        savedStateHandle = SavedStateHandle()
        savedStateHandle[DRAWING_SCREEN_STATE_KEY] = initialState
        viewModel =
            DrawingScreenViewModel(
                dispatchers = testDispatchers,
                savedStateHandle = savedStateHandle,
                drawingScreenUseCases = drawingScreenUseCases,
            )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetSettingsUseCase() =
        runTest(testDispatchers.testDispatcher) {
            drawingScreenUseCases.getSettingsUseCase().test {
                testDispatchers.testDispatcher.scheduler.advanceTimeBy(DELAY_BETWEEN_EMISSIONS)
                testDispatchers.testDispatcher.scheduler.runCurrent()
                awaitItem()
                coVerify {
                    SnackBarController.sendEvent(
                        event =
                            eq(
                                SnackbarEvent(
                                    message =
                                        UiText.StringResource(
                                            INT_MESSAGE,
                                            INT_MESSAGE_ARGS,
                                        ),
                                    action = SnackbarAction(name = UiText.StringResource(R.string.error)),
                                ),
                            ),
                    )
                }
                testDispatchers.testDispatcher.scheduler.advanceTimeBy(DELAY_BETWEEN_EMISSIONS)
                testDispatchers.testDispatcher.scheduler.runCurrent()
                awaitItem()
                coVerify {
                    SnackBarController.sendEvent(
                        event =
                            eq(
                                SnackbarEvent(
                                    message =
                                        UiText.StringResource(
                                            R.string.unable_to_get_settings,
                                            STRING_MESSAGE,
                                        ),
                                    action = SnackbarAction(name = UiText.StringResource(R.string.error)),
                                ),
                            ),
                    )
                }
                repeat(successfulSettingsEmissions.values.size) {
                    testDispatchers.testDispatcher.scheduler.advanceTimeBy(DELAY_BETWEEN_EMISSIONS)
                    testDispatchers.testDispatcher.scheduler.runCurrent()
                    val successfulResource = awaitItem() as Resource.Success
                    assertThat(viewModel.state.value.showFingerTracedLines)
                        .isEqualTo(successfulResource.data.showFingerTracedLines)
                    assertThat(viewModel.state.value.showApproximatedShape)
                        .isEqualTo(successfulResource.data.showApproximatedShape)
                }
                cancelAndConsumeRemainingEvents()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetSelectedShapeUseCase() =
        runTest(testDispatchers.testDispatcher) {
            drawingScreenUseCases.getSelectedShapeUseCase().test {
                testDispatchers.testDispatcher.scheduler.advanceTimeBy(
                    2 * DELAY_BETWEEN_EMISSIONS +
                        successfulSettingsEmissions.values.size * DELAY_BETWEEN_EMISSIONS +
                        DELAY_BETWEEN_EMISSIONS,
                )
                testDispatchers.testDispatcher.scheduler.runCurrent()
                awaitItem()
                coVerify {
                    SnackBarController.sendEvent(
                        event =
                            eq(
                                SnackbarEvent(
                                    message =
                                        UiText.StringResource(
                                            INT_MESSAGE,
                                            INT_MESSAGE_ARGS,
                                        ),
                                    action = SnackbarAction(name = UiText.StringResource(R.string.error)),
                                ),
                            ),
                    )
                }
                testDispatchers.testDispatcher.scheduler.advanceTimeBy(DELAY_BETWEEN_EMISSIONS)
                testDispatchers.testDispatcher.scheduler.runCurrent()
                awaitItem()
                coVerify {
                    SnackBarController.sendEvent(
                        event =
                            eq(
                                SnackbarEvent(
                                    message =
                                        UiText.StringResource(
                                            R.string.unable_to_get_selected_shape,
                                            STRING_MESSAGE,
                                        ),
                                    action = SnackbarAction(name = UiText.StringResource(R.string.error)),
                                ),
                            ),
                    )
                }
                repeat(Shape.entries.size) {
                    testDispatchers.testDispatcher.scheduler.advanceTimeBy(DELAY_BETWEEN_EMISSIONS)
                    testDispatchers.testDispatcher.scheduler.runCurrent()
                    val successfulResource = awaitItem() as Resource.Success
                    assertThat(viewModel.state.value.selectedShape)
                        .isEqualTo(successfulResource.data)
                }
                cancelAndConsumeRemainingEvents()
            }
        }

    companion object {
        @OptIn(ExperimentalCoroutinesApi::class)
        val testDispatchers = TestDispatchers()
        val successfulSettingsEmissions =
            Exhaustive.boolean().flatMap { showFingerTracedLines ->
                Exhaustive.boolean().map { showApproximatedShape ->
                    Resource.Success(
                        data =
                            Settings(
                                showFingerTracedLines = showFingerTracedLines,
                                showApproximatedShape = showApproximatedShape,
                            ),
                    )
                }
            }
        const val DELAY_BETWEEN_EMISSIONS = 1000L
        val initialState = DrawingScreenState()
        const val INT_MESSAGE = 0
        const val INT_MESSAGE_ARGS = 0
        const val STRING_MESSAGE = ""

        @OptIn(ExperimentalCoroutinesApi::class)
        val drawingScreenUseCases =
            mockk<DrawingScreenUseCases>(relaxed = true) {
                every { getSettingsUseCase() } returns
                    flow {
                        delay(DELAY_BETWEEN_EMISSIONS)
                        emit(
                            Resource.Error<Settings>(
                                message = MessageType.IntMessage(INT_MESSAGE, INT_MESSAGE_ARGS),
                            ),
                        )
                        delay(DELAY_BETWEEN_EMISSIONS)
                        emit(
                            Resource.Error<Settings>(
                                message = MessageType.StringMessage(STRING_MESSAGE),
                            ),
                        )
                        successfulSettingsEmissions.values.forEach {
                            delay(DELAY_BETWEEN_EMISSIONS)
                            emit(it)
                        }
                    }.flowOn(testDispatchers.testDispatcher)
                every { getSelectedShapeUseCase() } returns
                    flow {
                        delay(
                            2 * DELAY_BETWEEN_EMISSIONS +
                                successfulSettingsEmissions.values.size * DELAY_BETWEEN_EMISSIONS,
                        )
                        delay(DELAY_BETWEEN_EMISSIONS)
                        emit(
                            Resource.Error<Shape>(
                                message = MessageType.IntMessage(INT_MESSAGE, INT_MESSAGE_ARGS),
                            ),
                        )
                        delay(DELAY_BETWEEN_EMISSIONS)
                        emit(
                            Resource.Error<Shape>(
                                message = MessageType.StringMessage(STRING_MESSAGE),
                            ),
                        )
                        Shape.entries.map {
                            delay(DELAY_BETWEEN_EMISSIONS)
                            emit(
                                Resource.Success(
                                    data = it,
                                ),
                            )
                        }
                    }.flowOn(testDispatchers.testDispatcher)
            }
    }
}
