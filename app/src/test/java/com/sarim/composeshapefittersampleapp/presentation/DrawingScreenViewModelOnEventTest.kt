package com.sarim.composeshapefittersampleapp.presentation

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.sarim.compose_shape_fitter.shape.ApproximatedShape
import com.sarim.composeshapefittersampleapp.TestDispatchers
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.domain.usecase.UpdateSelectedShapeUseCase
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenViewModel.Companion.DRAWING_SCREEN_STATE_KEY
import io.kotest.assertions.fail
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

data class TestDataDrawingScreenViewModelOnEventTest(
    val inputEvent: DrawingScreenToViewModelEvents,
    val inputState: DrawingScreenState,
    val outputState: DrawingScreenState,
    val outputUseCase: Any?,
) {
    val testDescription = (
        "when the input event is $inputEvent " +
            "and the input state is $inputState " +
            "the output state is $outputState " +
            outputUseCase?.let { "and the usecase that will be called is $outputUseCase" }
    )
}

@RunWith(Parameterized::class)
class DrawingScreenViewModelOnEventTest(
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
                if (it is UpdateSelectedShapeUseCase) {
                    assertThat(testData.inputEvent).isInstanceOf(DrawingScreenToViewModelEvents.SetSelectedShape::class.java)
                    testDispatchers.testDispatcher.scheduler.advanceUntilIdle()
                    coVerify {
                        it(
                            selectedShape = (testData.inputEvent as DrawingScreenToViewModelEvents.SetSelectedShape).selectedShape,
                        )
                    }
                } else {
                    fail(
                        "$it is a usecase that is not one of these: " +
                            "[UpdateSelectedShapeUseCase]",
                    )
                }
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
                                ),
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent = DrawingScreenToViewModelEvents.SetDragging(false),
                                    inputState = DrawingScreenState(),
                                    outputState = DrawingScreenState(isDragging = false),
                                    outputUseCase = null,
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
                                ),
                            )
                        }
                        DrawingScreenToViewModelEvents.ToggleSettings::class -> {
                            DrawingScreenToViewModelEvents.ToggleSettings.Type.entries.flatMap { type ->
                                when (type) {
                                    DrawingScreenToViewModelEvents.ToggleSettings.Type.SHOW_FINGER_TRACED_LINES ->
                                        listOf(
                                            TestDataDrawingScreenViewModelOnEventTest(
                                                inputEvent = DrawingScreenToViewModelEvents.ToggleSettings(type),
                                                inputState = DrawingScreenState(showFingerTracedLines = true),
                                                outputState = DrawingScreenState(showFingerTracedLines = false),
                                                outputUseCase = null,
                                            ),
                                            TestDataDrawingScreenViewModelOnEventTest(
                                                inputEvent = DrawingScreenToViewModelEvents.ToggleSettings(type),
                                                inputState = DrawingScreenState(showFingerTracedLines = false),
                                                outputState = DrawingScreenState(showFingerTracedLines = true),
                                                outputUseCase = null,
                                            ),
                                        )
                                    DrawingScreenToViewModelEvents.ToggleSettings.Type.SHOW_APPROXIMATED_SHAPE ->
                                        listOf(
                                            TestDataDrawingScreenViewModelOnEventTest(
                                                inputEvent = DrawingScreenToViewModelEvents.ToggleSettings(type),
                                                inputState = DrawingScreenState(showApproximatedShape = true),
                                                outputState = DrawingScreenState(showApproximatedShape = false),
                                                outputUseCase = null,
                                            ),
                                            TestDataDrawingScreenViewModelOnEventTest(
                                                inputEvent = DrawingScreenToViewModelEvents.ToggleSettings(type),
                                                inputState = DrawingScreenState(showApproximatedShape = false),
                                                outputState = DrawingScreenState(showApproximatedShape = true),
                                                outputUseCase = null,
                                            ),
                                        )
                                }
                            }
                        }
                        DrawingScreenToViewModelEvents.ToggleSettingsDropDown::class -> {
                            listOf(
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent = DrawingScreenToViewModelEvents.ToggleSettingsDropDown,
                                    inputState = DrawingScreenState(showSettingsDropDown = true),
                                    outputState = DrawingScreenState(showSettingsDropDown = false),
                                    outputUseCase = null,
                                ),
                                TestDataDrawingScreenViewModelOnEventTest(
                                    inputEvent = DrawingScreenToViewModelEvents.ToggleSettingsDropDown,
                                    inputState = DrawingScreenState(showSettingsDropDown = false),
                                    outputState = DrawingScreenState(showSettingsDropDown = true),
                                    outputUseCase = null,
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
                                ),
                            )
                        }
                        else -> {
                            println("Warning: Unhandled sealed subclass ${it.simpleName}")
                            emptyList()
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
