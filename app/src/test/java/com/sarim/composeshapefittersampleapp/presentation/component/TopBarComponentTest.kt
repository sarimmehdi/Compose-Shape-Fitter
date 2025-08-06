package com.sarim.composeshapefittersampleapp.presentation.component

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenToViewModelEvents
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponentTestTags.APPROXIMATED_SHAPE
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponentTestTags.FINGER_TRACED_LINES
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponentTestTags.SETTINGS_ICON_BUTTON
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

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

        composeTestRule.onNodeWithTag(SETTINGS_ICON_BUTTON).performClick()

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
                data =
                    TopBarComponentData(
                        showSettingsDropDown = true,
                    ),
                onEvent = onEvent,
            )
        }

        composeTestRule.onNodeWithTag(FINGER_TRACED_LINES).performClick()
        composeTestRule.onNodeWithTag(APPROXIMATED_SHAPE).performClick()

        composeTestRule.runOnIdle {
            verifyOrder {
                onEvent(
                    eq(
                        DrawingScreenToViewModelEvents.ToggleSettings(
                            showFingerTracedLines = false,
                            showApproximatedShape = true,
                        ),
                    ),
                )
                onEvent(
                    eq(
                        DrawingScreenToViewModelEvents.ToggleSettings(
                            showFingerTracedLines = true,
                            showApproximatedShape = false,
                        ),
                    ),
                )
            }
        }
    }
}
