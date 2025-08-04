package com.sarim.composeshapefittersampleapp.presentation.component

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenToViewModelEvents
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.flatMap
import io.kotest.property.exhaustive.map
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RobolectricTestRunner
import kotlin.collections.map

@RunWith(RobolectricTestRunner::class)
class TopBarComponentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    val onEvent: (DrawingScreenToViewModelEvents) -> Unit = mockk(relaxed = true)

    @Test
    fun `click on settings icon`() {
        composeTestRule.setContent {
            TopBarComponent(
                onEvent = onEvent,
            )
        }

        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_ICON_BUTTON_TEST_TAG).performClick()

        composeTestRule.runOnIdle {
            verify {
                onEvent(ofType<DrawingScreenToViewModelEvents.ToggleSettingsDropDown>())
            }
        }
    }

    @Test
    fun `when the settings are open, click on all options`() {
        composeTestRule.setContent {
            TopBarComponent(
                data = TopBarComponentData(
                    showSettingsDropDown = true
                ),
                onEvent = onEvent,
            )
        }

        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG).performClick()
        composeTestRule.onNodeWithTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG).performClick()

        composeTestRule.runOnIdle {
            verifyOrder {
                onEvent(
                    eq(
                        DrawingScreenToViewModelEvents.ToggleSettings(
                            showFingerTracedLines = false,
                            showApproximatedShape = true,
                        )
                    )
                )
                onEvent(
                    eq(
                        DrawingScreenToViewModelEvents.ToggleSettings(
                            showFingerTracedLines = true,
                            showApproximatedShape = false,
                        )
                    )
                )
            }
        }
    }
}

data class TestDataTopBarComponentSettingsMenu(
    val data: TopBarComponentData,
    val enabledTestTags: List<String>,
    val disabledTestTags: List<String>,
) {
    val testDescription =
        "when input data is $data, " +
                "components with these test tags are enabled: $enabledTestTags" +
                "components with these test tags are disabled: $disabledTestTags"
}

@RunWith(ParameterizedRobolectricTestRunner::class)
class TopBarComponentTestSettingsMenuOpenClosed(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataTopBarComponentSettingsMenu,
) {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test() {
        composeTestRule.setContent {
            TopBarComponent(
                data = testData.data,
            )
        }

        composeTestRule.runOnIdle {
            testData.enabledTestTags.forEach {
                composeTestRule.onNodeWithTag(it, useUnmergedTree = true).assertExists()
            }

            testData.disabledTestTags.forEach {
                composeTestRule.onNodeWithTag(it, useUnmergedTree = true).assertDoesNotExist()
            }
        }
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(
            name = "{0}",
        )
        @Suppress("unused")
        fun getParameters(): Collection<Array<Any>> {
            return Exhaustive.boolean().flatMap { showSettingsDropDown ->
                Exhaustive.boolean().flatMap { showFingerTracedLines ->
                    Exhaustive.boolean().map { showApproximatedShape ->
                        TestDataTopBarComponentSettingsMenu(
                            data = TopBarComponentData(
                                showSettingsDropDown = showSettingsDropDown,
                                showFingerTracedLines = showFingerTracedLines,
                                showApproximatedShape = showApproximatedShape,
                            ),
                            enabledTestTags = if (showSettingsDropDown) {
                                listOf(
                                    TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG,
                                    TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG,
                                )
                                if (showFingerTracedLines) {
                                    listOf(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG)
                                } else {
                                    emptyList()
                                } + if (showApproximatedShape) {
                                    listOf(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG)
                                } else {
                                    emptyList()
                                }
                            } else {
                                emptyList()
                            },
                            disabledTestTags = if (!showSettingsDropDown) {
                                listOf(
                                    TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG,
                                    TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG,
                                    TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG,
                                    TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG
                                )
                            } else {
                                emptyList()
                            } + if (!showFingerTracedLines) {
                                listOf(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG)
                            } else {
                                emptyList()
                            } + if (!showApproximatedShape) {
                                listOf(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG)
                            } else {
                                emptyList()
                            }
                        )
                    }
                }
            }.values.map { data ->
                arrayOf(
                    data.testDescription,
                    data,
                )
            }
        }
    }
}
