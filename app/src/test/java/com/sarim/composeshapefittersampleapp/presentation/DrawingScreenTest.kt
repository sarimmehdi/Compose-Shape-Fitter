package com.sarim.composeshapefittersampleapp.presentation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.platform.app.InstrumentationRegistry
import com.sarim.composeshapefittersampleapp.TestDispatchers
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenViewModel.Companion.DRAWING_SCREEN_STATE_KEY
import com.sarim.composeshapefittersampleapp.presentation.component.CanvasComponentTestTags.CANVAS_COMPONENT
import com.sarim.composeshapefittersampleapp.presentation.component.DrawerComponentTestTags.CLOSE_DRAWER_ICON_BUTTON
import com.sarim.composeshapefittersampleapp.presentation.component.DrawerComponentTestTags.DRAWER_COMPONENT
import com.sarim.composeshapefittersampleapp.presentation.component.DrawerComponentTestTags.LAZY_COLUMN
import com.sarim.composeshapefittersampleapp.presentation.component.DrawerComponentTestTags.SELECTED_NAVIGATION_DRAWER_ITEM
import com.sarim.composeshapefittersampleapp.presentation.component.DrawerComponentTestTags.UNSELECTED_NAVIGATION_DRAWER_ITEM
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponentTestTags.APPROXIMATED_SHAPE
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponentTestTags.APPROXIMATED_SHAPE_TRAILING_ICON
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponentTestTags.FINGER_TRACED_LINES
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponentTestTags.FINGER_TRACED_LINES_TRAILING_ICON
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponentTestTags.OPEN_DRAWER_ICON_BUTTON
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponentTestTags.SETTINGS_DROP_DOWN_MENU
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponentTestTags.SETTINGS_ICON_BUTTON
import com.sarim.composeshapefittersampleapp.utils.SnackBarController
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.collections.zipWithNext

@RunWith(RobolectricTestRunner::class)
class DrawingScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    val testDispatchers = TestDispatchers()

    lateinit var drawingScreenUseCases: DrawingScreenUseCases
    lateinit var savedStateHandle: SavedStateHandle
    lateinit var viewModel: DrawingScreenViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        mockkObject(SnackBarController)
        savedStateHandle = SavedStateHandle()
        savedStateHandle[DRAWING_SCREEN_STATE_KEY] = DrawingScreenState()
        drawingScreenUseCases = mockk(relaxed = true)
        viewModel =
            spyk(
                DrawingScreenViewModel(
                    dispatchers = testDispatchers,
                    savedStateHandle = savedStateHandle,
                    drawingScreenUseCases = drawingScreenUseCases,
                ),
            )
        every {
            viewModel.onEvent(
                match { event -> event is DrawingScreenToViewModelEvents.ToggleSettings },
            )
        } answers {
            val toggleSettingsEvent = firstArg<DrawingScreenToViewModelEvents.ToggleSettings>()
            savedStateHandle[DRAWING_SCREEN_STATE_KEY] =
                (savedStateHandle[DRAWING_SCREEN_STATE_KEY] as DrawingScreenState?)?.copy(
                    showFingerTracedLines = toggleSettingsEvent.showFingerTracedLines,
                    showApproximatedShape = toggleSettingsEvent.showApproximatedShape,
                )
        }
        every {
            viewModel.onEvent(
                match { event -> event is DrawingScreenToViewModelEvents.SetSelectedShape },
            )
        } answers {
            savedStateHandle[DRAWING_SCREEN_STATE_KEY] =
                (savedStateHandle[DRAWING_SCREEN_STATE_KEY] as DrawingScreenState?)?.copy(
                    selectedShape = firstArg<DrawingScreenToViewModelEvents.SetSelectedShape>().selectedShape,
                )
        }
    }

    @Test
    fun `test open and closing behavior of drawer`() {
        composeTestRule.setContent {
            val drawingScreenState by viewModel.state.collectAsStateWithLifecycle()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            DrawingScreen(
                data =
                    DrawingScreenData(
                        state = drawingScreenState,
                        drawerState = drawerState,
                    ),
                onEvent = viewModel::onEvent,
            )
        }

        composeTestRule.onNodeWithTag(DRAWER_COMPONENT).assertIsNotDisplayed()

        composeTestRule.onNodeWithTag(OPEN_DRAWER_ICON_BUTTON).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(DRAWER_COMPONENT).assertIsDisplayed()
        }

        composeTestRule.onNodeWithTag(CLOSE_DRAWER_ICON_BUTTON).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(DRAWER_COMPONENT).assertIsNotDisplayed()
        }

        composeTestRule.onNodeWithTag(OPEN_DRAWER_ICON_BUTTON).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(DRAWER_COMPONENT).assertIsDisplayed()
        }

        composeTestRule.onNodeWithTag(OPEN_DRAWER_ICON_BUTTON).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(DRAWER_COMPONENT).assertIsNotDisplayed()
        }
    }

    @Test
    fun `test open and closing behavior of settings menu`() {
        composeTestRule.setContent {
            val drawingScreenState by viewModel.state.collectAsStateWithLifecycle()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            DrawingScreen(
                data =
                    DrawingScreenData(
                        state = drawingScreenState,
                        drawerState = drawerState,
                    ),
                onEvent = viewModel::onEvent,
            )
        }

        composeTestRule.onNodeWithTag(SETTINGS_DROP_DOWN_MENU).assertIsNotDisplayed()

        composeTestRule.onNodeWithTag(SETTINGS_ICON_BUTTON).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(SETTINGS_DROP_DOWN_MENU).assertIsDisplayed()
            composeTestRule.onNodeWithTag(FINGER_TRACED_LINES).assertIsDisplayed()
            composeTestRule.onNodeWithTag(APPROXIMATED_SHAPE).assertIsDisplayed()
        }

        composeTestRule.onNodeWithTag(SETTINGS_ICON_BUTTON).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(SETTINGS_DROP_DOWN_MENU).assertIsNotDisplayed()
            composeTestRule.onNodeWithTag(FINGER_TRACED_LINES).assertIsNotDisplayed()
            composeTestRule.onNodeWithTag(APPROXIMATED_SHAPE).assertIsNotDisplayed()
        }
    }

    @Test
    fun `test all settings in settings menu by clicking on them`() {
        composeTestRule.setContent {
            val drawingScreenState by viewModel.state.collectAsStateWithLifecycle()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            DrawingScreen(
                data =
                    DrawingScreenData(
                        state = drawingScreenState,
                        drawerState = drawerState,
                    ),
                onEvent = viewModel::onEvent,
            )
        }

        composeTestRule.onNodeWithTag(SETTINGS_DROP_DOWN_MENU).assertIsNotDisplayed()

        composeTestRule.onNodeWithTag(SETTINGS_ICON_BUTTON).performClick()

        composeTestRule.runOnIdle {
            composeTestRule
                .onNodeWithTag(
                    FINGER_TRACED_LINES_TRAILING_ICON,
                    useUnmergedTree = true,
                ).assertIsDisplayed()
            composeTestRule
                .onNodeWithTag(
                    APPROXIMATED_SHAPE_TRAILING_ICON,
                    useUnmergedTree = true,
                ).assertIsDisplayed()
            composeTestRule
                .onNodeWithTag(
                    CANVAS_COMPONENT +
                        "_isDragging=false" +
                        "_showFingerTracedLines=true" +
                        "_showApproximatedShape=true",
                    useUnmergedTree = true,
                ).assertExists()
        }

        composeTestRule.onNodeWithTag(FINGER_TRACED_LINES).performClick()

        composeTestRule.runOnIdle {
            composeTestRule
                .onNodeWithTag(
                    FINGER_TRACED_LINES_TRAILING_ICON,
                    useUnmergedTree = true,
                ).assertIsNotDisplayed()
            composeTestRule
                .onNodeWithTag(
                    APPROXIMATED_SHAPE_TRAILING_ICON,
                    useUnmergedTree = true,
                ).assertIsDisplayed()
            composeTestRule
                .onNodeWithTag(
                    CANVAS_COMPONENT +
                        "_isDragging=false" +
                        "_showFingerTracedLines=false" +
                        "_showApproximatedShape=true",
                    useUnmergedTree = true,
                ).assertExists()
        }

        composeTestRule.onNodeWithTag(APPROXIMATED_SHAPE).performClick()

        composeTestRule.runOnIdle {
            composeTestRule
                .onNodeWithTag(
                    FINGER_TRACED_LINES_TRAILING_ICON,
                    useUnmergedTree = true,
                ).assertIsNotDisplayed()
            composeTestRule
                .onNodeWithTag(
                    APPROXIMATED_SHAPE_TRAILING_ICON,
                    useUnmergedTree = true,
                ).assertIsNotDisplayed()
            composeTestRule
                .onNodeWithTag(
                    CANVAS_COMPONENT +
                        "_isDragging=false" +
                        "_showFingerTracedLines=false" +
                        "_showApproximatedShape=false",
                    useUnmergedTree = true,
                ).assertExists()
        }

        composeTestRule.onNodeWithTag(FINGER_TRACED_LINES).performClick()

        composeTestRule.runOnIdle {
            composeTestRule
                .onNodeWithTag(
                    FINGER_TRACED_LINES_TRAILING_ICON,
                    useUnmergedTree = true,
                ).assertIsDisplayed()
            composeTestRule
                .onNodeWithTag(
                    APPROXIMATED_SHAPE_TRAILING_ICON,
                    useUnmergedTree = true,
                ).assertIsNotDisplayed()
            composeTestRule
                .onNodeWithTag(
                    CANVAS_COMPONENT +
                        "_isDragging=false" +
                        "_showFingerTracedLines=true" +
                        "_showApproximatedShape=false",
                    useUnmergedTree = true,
                ).assertExists()
        }

        composeTestRule.onNodeWithTag(APPROXIMATED_SHAPE).performClick()

        composeTestRule.runOnIdle {
            composeTestRule
                .onNodeWithTag(
                    FINGER_TRACED_LINES_TRAILING_ICON,
                    useUnmergedTree = true,
                ).assertIsDisplayed()
            composeTestRule
                .onNodeWithTag(
                    APPROXIMATED_SHAPE_TRAILING_ICON,
                    useUnmergedTree = true,
                ).assertIsDisplayed()
            composeTestRule
                .onNodeWithTag(
                    CANVAS_COMPONENT +
                        "_isDragging=false" +
                        "_showFingerTracedLines=true" +
                        "_showApproximatedShape=true",
                    useUnmergedTree = true,
                ).assertExists()
        }

        composeTestRule.onNodeWithTag(SETTINGS_ICON_BUTTON).performClick()
        composeTestRule.runOnIdle {
            composeTestRule.onRoot().performTouchInput {
                down(Offset(100f, 100f))
                moveTo(Offset(50f, 100f))
            }
        }
        composeTestRule
            .onNodeWithTag(
                CANVAS_COMPONENT +
                    "_isDragging=true" +
                    "_showFingerTracedLines=true" +
                    "_showApproximatedShape=true",
                useUnmergedTree = true,
            ).assertExists()
        composeTestRule.onRoot().performTouchInput {
            up()
        }
        composeTestRule
            .onNodeWithTag(
                CANVAS_COMPONENT +
                    "_isDragging=false" +
                    "_showFingerTracedLines=true" +
                    "_showApproximatedShape=true",
                useUnmergedTree = true,
            ).assertExists()
    }

    @Test
    fun `test clicking on all shapes in drawer`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            val drawingScreenState by viewModel.state.collectAsStateWithLifecycle()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            DrawingScreen(
                data =
                    DrawingScreenData(
                        state = drawingScreenState,
                        drawerState = drawerState,
                    ),
                onEvent = viewModel::onEvent,
            )
        }

        composeTestRule.onNodeWithTag(OPEN_DRAWER_ICON_BUTTON).performClick()

        Shape.entries.zipWithNext().map { consecutiveShapePairs ->
            val selectedShape = consecutiveShapePairs.first
            val shapeNotSelectedButWillBeSelectedNext = consecutiveShapePairs.second

            composeTestRule.runOnIdle {
                composeTestRule
                    .onNodeWithTag(
                        LAZY_COLUMN,
                        useUnmergedTree = true,
                    ).performScrollToNode(
                        hasTestTag(
                            SELECTED_NAVIGATION_DRAWER_ITEM + context.getString(selectedShape.shapeStringId),
                        ),
                    )
                Shape.entries.filter { it != selectedShape }.forEach { notSelectedShape ->
                    composeTestRule
                        .onNodeWithTag(
                            LAZY_COLUMN,
                            useUnmergedTree = true,
                        ).performScrollToNode(
                            hasTestTag(
                                UNSELECTED_NAVIGATION_DRAWER_ITEM +
                                    context.getString(notSelectedShape.shapeStringId),
                            ),
                        )
                }
                composeTestRule
                    .onNodeWithTag(
                        LAZY_COLUMN,
                        useUnmergedTree = true,
                    ).performScrollToNode(
                        hasTestTag(
                            SELECTED_NAVIGATION_DRAWER_ITEM + context.getString(selectedShape.shapeStringId),
                        ),
                    )
                composeTestRule
                    .onNodeWithTag(
                        SELECTED_NAVIGATION_DRAWER_ITEM + context.getString(selectedShape.shapeStringId),
                        useUnmergedTree = true,
                    ).performClick()
            }

            composeTestRule.runOnIdle {
                composeTestRule.onNodeWithTag(OPEN_DRAWER_ICON_BUTTON).performClick()
            }

            composeTestRule.runOnIdle {
                composeTestRule
                    .onNodeWithTag(
                        LAZY_COLUMN,
                        useUnmergedTree = true,
                    ).performScrollToNode(
                        hasTestTag(
                            SELECTED_NAVIGATION_DRAWER_ITEM + context.getString(selectedShape.shapeStringId),
                        ),
                    )
                Shape.entries.filter { it != selectedShape }.forEach { notSelectedShape ->
                    composeTestRule
                        .onNodeWithTag(
                            LAZY_COLUMN,
                            useUnmergedTree = true,
                        ).performScrollToNode(
                            hasTestTag(
                                UNSELECTED_NAVIGATION_DRAWER_ITEM +
                                    context.getString(notSelectedShape.shapeStringId),
                            ),
                        )
                }
                composeTestRule
                    .onNodeWithTag(
                        LAZY_COLUMN,
                        useUnmergedTree = true,
                    ).performScrollToNode(
                        hasTestTag(
                            UNSELECTED_NAVIGATION_DRAWER_ITEM +
                                context.getString(shapeNotSelectedButWillBeSelectedNext.shapeStringId),
                        ),
                    )
                composeTestRule
                    .onNodeWithTag(
                        UNSELECTED_NAVIGATION_DRAWER_ITEM +
                            context.getString(shapeNotSelectedButWillBeSelectedNext.shapeStringId),
                        useUnmergedTree = true,
                    ).performClick()
            }

            composeTestRule.runOnIdle {
                composeTestRule
                    .onNodeWithTag(
                        OPEN_DRAWER_ICON_BUTTON,
                        useUnmergedTree = true,
                    ).performClick()
            }
        }
    }
}
