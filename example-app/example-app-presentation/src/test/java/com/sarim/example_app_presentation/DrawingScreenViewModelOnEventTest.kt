package com.sarim.example_app_presentation

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.sarim.compose_shape_fitter.shape.ApproximatedShape
import com.sarim.example_app_domain.model.Settings
import com.sarim.example_app_domain.model.Shape
import com.sarim.example_app_domain.usecase.UpdateSelectedShapeUseCase
import com.sarim.example_app_domain.usecase.UpdateSettingsUseCase
import com.sarim.example_app_presentation.DrawingScreenViewModel.Companion.DRAWING_SCREEN_STATE_KEY
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.flatMap
import io.kotest.property.exhaustive.map
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.fail
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

internal data class TestDataDrawingScreenViewModelOnEventTest(
    val inputEvent: DrawingScreenToViewModelEvents,
    val inputState: DrawingScreenState,
    val outputState: DrawingScreenState,
    val outputUseCase: Any?,
    val outputEvent: (suspend () -> Unit)?,
) {
    val testDescription = (
        "when the input event is $inputEvent \n" +
            "and the input state is $inputState \n" +
            "the output state is $outputState \n" +
            outputUseCase?.let { "and the usecase that will be called is $outputUseCase \n" } +
            outputEvent?.let { "and the event that will occur is $outputEvent" }
    )
}

@RunWith(Parameterized::class)
internal class DrawingScreenViewModelOnEventTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataDrawingScreenViewModelOnEventTest,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    lateinit var testDispatchers: TestDispatchers

    lateinit var savedStateHandle: SavedStateHandle
    lateinit var viewModel: DrawingScreenViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        testDispatchers = TestDispatchers()
        savedStateHandle = SavedStateHandle()
        savedStateHandle[DRAWING_SCREEN_STATE_KEY] = testData.inputState
        viewModel =
            DrawingScreenViewModel(
                dispatchers = testDispatchers,
                savedStateHandle = savedStateHandle,
                drawingScreenUseCases = drawingScreenUseCases,
            )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test() =
        runTest {
            assertThat(viewModel.state.value).isEqualTo(testData.inputState)
            viewModel.onEvent(testData.inputEvent)
            assertThat(viewModel.state.value).isEqualTo(testData.outputState)
            testData.outputUseCase?.let {
                when (it) {
                    is UpdateSelectedShapeUseCase -> {
                        assertThat(testData.inputEvent)
                            .isInstanceOf(DrawingScreenToViewModelEvents.SetSelectedShape::class.java)
                        testDispatchers.testDispatcher.scheduler.advanceUntilIdle()
                        coVerify {
                            val inputEvent = testData.inputEvent as DrawingScreenToViewModelEvents.SetSelectedShape
                            it(
                                selectedShape = inputEvent.selectedShape,
                            )
                        }
                    }
                    is UpdateSettingsUseCase -> {
                        assertThat(testData.inputEvent)
                            .isInstanceOf(DrawingScreenToViewModelEvents.ToggleSettings::class.java)
                        testDispatchers.testDispatcher.scheduler.advanceUntilIdle()
                        coVerify {
                            val inputEvent = testData.inputEvent as DrawingScreenToViewModelEvents.ToggleSettings
                            it(
                                settings =
                                    Settings(
                                        showFingerTracedLines = inputEvent.showFingerTracedLines,
                                        showApproximatedShape = inputEvent.showApproximatedShape,
                                    ),
                            )
                        }
                    }
                    else -> {
                        fail(
                            "unexpected usecase: $it",
                        )
                    }
                }
            }
            testData.outputEvent?.let {
                coEvery { it() }
            }
        }

    companion object {
        val drawingScreenUseCases =
            mockk<DrawingScreenUseCases>(relaxed = true) {
                every { getSettingsUseCase() } returns emptyFlow()
                every { getSelectedShapeUseCase() } returns emptyFlow()
            }

        @JvmStatic
        @Parameterized.Parameters(
            name = "{0}",
        )
        @Suppress("unused", "LongMethod")
        fun getParameters(): Collection<Array<Any>> {
            val testDataList =
                DrawingScreenToViewModelEvents::class.sealedSubclasses.flatMap {
                    when (it) {
                        DrawingScreenToViewModelEvents.SetSelectedShape::class -> {
                            Shape.entries.map { shape ->
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent = DrawingScreenToViewModelEvents.SetSelectedShape(shape),
                                    inputState = DrawingScreenState(),
                                    outputState = DrawingScreenState(),
                                    outputUseCase = drawingScreenUseCases.updateSelectedShapeUseCase,
                                    outputEvent = null,
                                )
                            }
                        }
                        DrawingScreenToViewModelEvents.SetDragging::class -> {
                            listOf(
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent = DrawingScreenToViewModelEvents.SetDragging(true),
                                    inputState = DrawingScreenState(),
                                    outputState = DrawingScreenState(isDragging = true),
                                    outputUseCase = null,
                                    outputEvent = null,
                                ),
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent = DrawingScreenToViewModelEvents.SetDragging(false),
                                    inputState = DrawingScreenState(),
                                    outputState = DrawingScreenState(isDragging = false),
                                    outputUseCase = null,
                                    outputEvent = null,
                                ),
                            )
                        }
                        DrawingScreenToViewModelEvents.SetLines::class -> {
                            listOf(
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent =
                                        DrawingScreenToViewModelEvents.SetLines(
                                            persistentListOf(
                                                Pair(Offset.Infinite, Offset.Zero),
                                                Pair(Offset.Zero, Offset.Zero),
                                                Pair(Offset.Zero, Offset.Infinite),
                                            ),
                                        ),
                                    inputState = DrawingScreenState(),
                                    outputState =
                                        DrawingScreenState(
                                            lines =
                                                persistentListOf(
                                                    Pair(Offset.Infinite, Offset.Zero),
                                                    Pair(Offset.Zero, Offset.Zero),
                                                    Pair(Offset.Zero, Offset.Infinite),
                                                ),
                                        ),
                                    outputUseCase = null,
                                    outputEvent = null,
                                ),
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent =
                                        DrawingScreenToViewModelEvents.SetLines(
                                            persistentListOf(
                                                Pair(Offset.Infinite, Offset.Zero),
                                                Pair(Offset.Zero, Offset.Zero),
                                                Pair(Offset.Zero, Offset.Infinite),
                                            ),
                                        ),
                                    inputState =
                                        DrawingScreenState(
                                            lines =
                                                persistentListOf(
                                                    Pair(Offset.Zero, Offset.Zero),
                                                    Pair(Offset.Zero, Offset.Infinite),
                                                ),
                                        ),
                                    outputState =
                                        DrawingScreenState(
                                            lines =
                                                persistentListOf(
                                                    Pair(Offset.Infinite, Offset.Zero),
                                                    Pair(Offset.Zero, Offset.Zero),
                                                    Pair(Offset.Zero, Offset.Infinite),
                                                ),
                                        ),
                                    outputUseCase = null,
                                    outputEvent = null,
                                ),
                            )
                        }
                        DrawingScreenToViewModelEvents.UpdateLines::class -> {
                            listOf(
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent =
                                        DrawingScreenToViewModelEvents.UpdateLines(
                                            Pair(Offset.Infinite, Offset.Zero),
                                        ),
                                    inputState = DrawingScreenState(),
                                    outputState =
                                        DrawingScreenState(
                                            lines =
                                                persistentListOf(
                                                    Pair(Offset.Infinite, Offset.Zero),
                                                ),
                                        ),
                                    outputUseCase = null,
                                    outputEvent = null,
                                ),
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent =
                                        DrawingScreenToViewModelEvents.UpdateLines(
                                            Pair(Offset.Infinite, Offset.Zero),
                                        ),
                                    inputState =
                                        DrawingScreenState(
                                            lines =
                                                persistentListOf(
                                                    Pair(Offset.Zero, Offset.Infinite),
                                                    Pair(Offset.Infinite, Offset.Zero),
                                                ),
                                        ),
                                    outputState =
                                        DrawingScreenState(
                                            lines =
                                                persistentListOf(
                                                    Pair(Offset.Zero, Offset.Infinite),
                                                    Pair(Offset.Infinite, Offset.Zero),
                                                    Pair(Offset.Infinite, Offset.Zero),
                                                ),
                                        ),
                                    outputUseCase = null,
                                    outputEvent = null,
                                ),
                            )
                        }
                        DrawingScreenToViewModelEvents.SetPoints::class -> {
                            listOf(
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent =
                                        DrawingScreenToViewModelEvents.SetPoints(
                                            persistentListOf(Offset.Infinite, Offset.Zero, Offset.Zero),
                                        ),
                                    inputState = DrawingScreenState(),
                                    outputState =
                                        DrawingScreenState(
                                            points = persistentListOf(Offset.Infinite, Offset.Zero, Offset.Zero),
                                        ),
                                    outputUseCase = null,
                                    outputEvent = null,
                                ),
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent =
                                        DrawingScreenToViewModelEvents.SetPoints(
                                            persistentListOf(Offset.Infinite, Offset.Zero, Offset.Zero),
                                        ),
                                    inputState =
                                        DrawingScreenState(
                                            points = persistentListOf(Offset.Infinite, Offset.Zero, Offset.Infinite),
                                        ),
                                    outputState =
                                        DrawingScreenState(
                                            points = persistentListOf(Offset.Infinite, Offset.Zero, Offset.Zero),
                                        ),
                                    outputUseCase = null,
                                    outputEvent = null,
                                ),
                            )
                        }
                        DrawingScreenToViewModelEvents.ToggleSettings::class -> {
                            Exhaustive
                                .boolean()
                                .flatMap { showFingerTracedLines ->
                                    Exhaustive.boolean().map { showApproximatedShape ->
                                        TestDataDrawingScreenViewModelOnEventTest(
                                            inputEvent =
                                                DrawingScreenToViewModelEvents.ToggleSettings(
                                                    showFingerTracedLines = showFingerTracedLines,
                                                    showApproximatedShape = showApproximatedShape,
                                                ),
                                            inputState = DrawingScreenState(),
                                            outputState = DrawingScreenState(),
                                            outputUseCase = drawingScreenUseCases.updateSettingsUseCase,
                                            outputEvent = null,
                                        )
                                    }
                                }.values
                        }
                        DrawingScreenToViewModelEvents.ToggleSettingsDropDown::class -> {
                            listOf(
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent = DrawingScreenToViewModelEvents.ToggleSettingsDropDown,
                                    inputState = DrawingScreenState(showSettingsDropDown = true),
                                    outputState = DrawingScreenState(showSettingsDropDown = false),
                                    outputUseCase = null,
                                    outputEvent = null,
                                ),
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent = DrawingScreenToViewModelEvents.ToggleSettingsDropDown,
                                    inputState = DrawingScreenState(showSettingsDropDown = false),
                                    outputState = DrawingScreenState(showSettingsDropDown = true),
                                    outputUseCase = null,
                                    outputEvent = null,
                                ),
                            )
                        }
                        DrawingScreenToViewModelEvents.UpdatePoints::class -> {
                            listOf(
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent =
                                        DrawingScreenToViewModelEvents.UpdatePoints(
                                            Offset.Infinite,
                                        ),
                                    inputState = DrawingScreenState(),
                                    outputState =
                                        DrawingScreenState(
                                            points = persistentListOf(Offset.Infinite),
                                        ),
                                    outputUseCase = null,
                                    outputEvent = null,
                                ),
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent =
                                        DrawingScreenToViewModelEvents.UpdatePoints(
                                            Offset.Infinite,
                                        ),
                                    inputState =
                                        DrawingScreenState(
                                            points = persistentListOf(Offset.Infinite, Offset.Zero, Offset.Infinite),
                                        ),
                                    outputState =
                                        DrawingScreenState(
                                            points =
                                                persistentListOf(
                                                    Offset.Infinite,
                                                    Offset.Zero,
                                                    Offset.Infinite,
                                                    Offset.Infinite,
                                                ),
                                        ),
                                    outputUseCase = null,
                                    outputEvent = null,
                                ),
                            )
                        }
                        DrawingScreenToViewModelEvents.SetApproximateShape::class -> {
                            val approximatedShape = mockk<ApproximatedShape>(relaxed = true)
                            listOf(
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent =
                                        DrawingScreenToViewModelEvents.SetApproximateShape(
                                            approximatedShape,
                                        ),
                                    inputState = DrawingScreenState(),
                                    outputState =
                                        DrawingScreenState(
                                            approximatedShape = approximatedShape,
                                        ),
                                    outputUseCase = null,
                                    outputEvent = null,
                                ),
                            )
                        }
                        else -> {
                            error(
                                "Warning: Unhandled sealed subclass ${it.simpleName}. " +
                                    "Please handle this new event type.",
                            )
                        }
                    }
                }

            return testDataList.map { data ->
                arrayOf(
                    data.testDescription,
                    data,
                )
            }
        }
    }
}
