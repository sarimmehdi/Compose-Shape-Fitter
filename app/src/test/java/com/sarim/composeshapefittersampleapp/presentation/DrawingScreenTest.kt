package com.sarim.composeshapefittersampleapp.presentation

import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sarim.composeshapefittersampleapp.TestDispatchers
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenViewModel.Companion.DRAWING_SCREEN_STATE_KEY
import com.sarim.composeshapefittersampleapp.presentation.component.CANVAS_COMPONENT_TEST_TAG
import com.sarim.composeshapefittersampleapp.presentation.component.DRAWER_COMPONENT_CLOSE_DRAWER_ICON_BUTTON_TEST_TAG
import com.sarim.composeshapefittersampleapp.presentation.component.DRAWER_COMPONENT_TEST_TAG
import com.sarim.composeshapefittersampleapp.presentation.component.TOP_BAR_COMPONENT_OPEN_DRAWER_ICON_BUTTON_TEST_TAG
import com.sarim.composeshapefittersampleapp.presentation.component.TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG
import com.sarim.composeshapefittersampleapp.presentation.component.TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG
import com.sarim.composeshapefittersampleapp.presentation.component.TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG
import com.sarim.composeshapefittersampleapp.presentation.component.TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG
import com.sarim.composeshapefittersampleapp.presentation.component.TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_TEST_TAG
import com.sarim.composeshapefittersampleapp.presentation.component.TOP_BAR_COMPONENT_SETTINGS_ICON_BUTTON_TEST_TAG
import com.sarim.composeshapefittersampleapp.utils.Resource
import com.sarim.composeshapefittersampleapp.utils.SnackBarController
import io.mockk.coEvery
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

@RunWith(RobolectricTestRunner::class)
class DrawingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    val testDispatchers = TestDispatchers()

    lateinit var savedStateHandle: SavedStateHandle
    lateinit var viewModel: DrawingScreenViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        mockkObject(SnackBarController)
        savedStateHandle = SavedStateHandle()
        savedStateHandle[DRAWING_SCREEN_STATE_KEY] = DrawingScreenState()
        viewModel =
            spyk(
                DrawingScreenViewModel(
                    dispatchers = testDispatchers,
                    savedStateHandle = savedStateHandle,
                    drawingScreenUseCases = mockk(relaxed = true),
                )
            )
        every { viewModel.onEvent(match { event -> event is DrawingScreenToViewModelEvents.ToggleSettings }) } answers {
            savedStateHandle[DRAWING_SCREEN_STATE_KEY] =
                (savedStateHandle[DRAWING_SCREEN_STATE_KEY] as DrawingScreenState?)?.copy(
                    showFingerTracedLines = firstArg<DrawingScreenToViewModelEvents.ToggleSettings>().showFingerTracedLines,
                    showApproximatedShape = firstArg<DrawingScreenToViewModelEvents.ToggleSettings>().showApproximatedShape
                )
        }
    }

    @Test
    fun `test open and closing behavior of drawer`() {
        composeTestRule.setContent {
            val drawingScreenState by viewModel.state.collectAsStateWithLifecycle()
            DrawingScreen(
                state = drawingScreenState,
                onEvent = viewModel::onEvent
            )
        }

        composeTestRule.onNodeWithTag(DRAWER_COMPONENT_TEST_TAG).assertIsNotDisplayed()

        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_OPEN_DRAWER_ICON_BUTTON_TEST_TAG).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(DRAWER_COMPONENT_TEST_TAG).assertIsDisplayed()
        }

        composeTestRule.onNodeWithTag(DRAWER_COMPONENT_CLOSE_DRAWER_ICON_BUTTON_TEST_TAG).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(DRAWER_COMPONENT_TEST_TAG).assertIsNotDisplayed()
        }

        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_OPEN_DRAWER_ICON_BUTTON_TEST_TAG).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(DRAWER_COMPONENT_TEST_TAG).assertIsDisplayed()
        }

        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_OPEN_DRAWER_ICON_BUTTON_TEST_TAG).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(DRAWER_COMPONENT_TEST_TAG).assertIsNotDisplayed()
        }
    }

    @Test
    fun `test open and closing behavior of settings menu`() {
        composeTestRule.setContent {
            val drawingScreenState by viewModel.state.collectAsStateWithLifecycle()
            DrawingScreen(
                state = drawingScreenState,
                onEvent = viewModel::onEvent
            )
        }

        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_TEST_TAG).assertIsNotDisplayed()

        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_ICON_BUTTON_TEST_TAG).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_TEST_TAG).assertIsDisplayed()
            composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG).assertIsDisplayed()
            composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG).assertIsDisplayed()
        }

        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_ICON_BUTTON_TEST_TAG).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_TEST_TAG).assertIsNotDisplayed()
            composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG).assertIsNotDisplayed()
            composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG).assertIsNotDisplayed()
        }
    }

    @Test
    fun `test all settings in settings menu by clicking on them`() {
        composeTestRule.setContent {
            val drawingScreenState by viewModel.state.collectAsStateWithLifecycle()
            DrawingScreen(
                state = drawingScreenState,
                onEvent = viewModel::onEvent
            )
        }

        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_TEST_TAG).assertIsNotDisplayed()

        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_ICON_BUTTON_TEST_TAG).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(
                TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG,
                useUnmergedTree = true
            ).assertIsDisplayed()
            composeTestRule.onNodeWithTag(
                TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG,
                useUnmergedTree = true
            ).assertIsDisplayed()
            composeTestRule.onNodeWithTag(
                CANVAS_COMPONENT_TEST_TAG +
                        "_isDragging=false" +
                        "_showFingerTracedLines=true" +
                        "_showApproximatedShape=true",
                useUnmergedTree = true
            ).assertExists()
        }

        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(
                TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG,
                useUnmergedTree = true
            ).assertIsNotDisplayed()
            composeTestRule.onNodeWithTag(
                TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG,
                useUnmergedTree = true
            ).assertIsDisplayed()
            composeTestRule.onNodeWithTag(
                CANVAS_COMPONENT_TEST_TAG +
                        "_isDragging=false" +
                        "_showFingerTracedLines=false" +
                        "_showApproximatedShape=true",
                useUnmergedTree = true
            ).assertExists()
        }

        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(
                TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG,
                useUnmergedTree = true
            ).assertIsNotDisplayed()
            composeTestRule.onNodeWithTag(
                TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG,
                useUnmergedTree = true
            ).assertIsNotDisplayed()
            composeTestRule.onNodeWithTag(
                CANVAS_COMPONENT_TEST_TAG +
                        "_isDragging=false" +
                        "_showFingerTracedLines=false" +
                        "_showApproximatedShape=false",
                useUnmergedTree = true
            ).assertExists()
        }

        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(
                TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG,
                useUnmergedTree = true
            ).assertIsDisplayed()
            composeTestRule.onNodeWithTag(
                TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG,
                useUnmergedTree = true
            ).assertIsNotDisplayed()
            composeTestRule.onNodeWithTag(
                CANVAS_COMPONENT_TEST_TAG +
                        "_isDragging=false" +
                        "_showFingerTracedLines=true" +
                        "_showApproximatedShape=false",
                useUnmergedTree = true
            ).assertExists()
        }

        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG).performClick()

        composeTestRule.runOnIdle {
            composeTestRule.onNodeWithTag(
                TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG,
                useUnmergedTree = true
            ).assertIsDisplayed()
            composeTestRule.onNodeWithTag(
                TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG,
                useUnmergedTree = true
            ).assertIsDisplayed()
            composeTestRule.onNodeWithTag(
                CANVAS_COMPONENT_TEST_TAG +
                        "_isDragging=false" +
                        "_showFingerTracedLines=true" +
                        "_showApproximatedShape=true",
                useUnmergedTree = true
            ).assertExists()
        }

        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_ICON_BUTTON_TEST_TAG).performClick()
        composeTestRule.runOnIdle {
            composeTestRule.onRoot().performTouchInput {
                down(Offset(100f, 100f))
                moveTo(Offset(50f, 100f))
            }
        }
        composeTestRule.onNodeWithTag(
            CANVAS_COMPONENT_TEST_TAG +
                    "_isDragging=true" +
                    "_showFingerTracedLines=true" +
                    "_showApproximatedShape=true",
            useUnmergedTree = true
        ).assertExists()
        composeTestRule.onRoot().performTouchInput {
            up()
        }
        composeTestRule.onNodeWithTag(
            CANVAS_COMPONENT_TEST_TAG +
                    "_isDragging=false" +
                    "_showFingerTracedLines=true" +
                    "_showApproximatedShape=true",
            useUnmergedTree = true
        ).assertExists()
    }

    // TODO: only test interaction between the two separate components and then add jacoco!
    @Test
    fun `test clicking on all shapes in drawer`() {

    }
}