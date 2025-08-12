package com.sarim.test_app.test

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.common.truth.Truth.assertThat
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.APPROXIMATED_SHAPE
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.APPROXIMATED_SHAPE_TRAILING_ICON
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.FINGER_TRACED_LINES
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.FINGER_TRACED_LINES_TRAILING_ICON
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.SETTINGS_ICON_BUTTON
import com.sarim.test_app.startApp
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

data class TestDataDrawingScreenTest(
    val testTagToClickOn: String,
    val affectedTestTagAfterClick: String,
    val enabledTestTags: List<String>,
    val disabledTestTags: List<String>,
) {
    val testDescription =
        "when input node with $testTagToClickOn is clicked, \n" +
            "the node with test tag $affectedTestTagAfterClick will be affected, \n" +
            "these test tags must be enabled before the click action: $enabledTestTags, and \n" +
            "these test tags must be disabled before the click action: $disabledTestTags"
}

@RunWith(Parameterized::class)
class DrawingScreenSettingsTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataDrawingScreenTest,
) {
    @Test
    fun test() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.startApp()

        device.wait(Until.hasObject(By.res(SETTINGS_ICON_BUTTON)), MAX_TIMEOUT)
        device.findObject(By.res(SETTINGS_ICON_BUTTON)).click()

        testData.enabledTestTags.forEach {
            assertThat(
                device.wait(Until.hasObject(By.res(it)), MAX_TIMEOUT),
            ).isTrue()
        }

        testData.disabledTestTags.forEach {
            assertThat(
                device.wait(Until.hasObject(By.res(it)), MAX_TIMEOUT),
            ).isFalse()
        }

        device.findObject(By.res(testData.testTagToClickOn)).click()
    }

    companion object {
        private const val MAX_TIMEOUT = 10_000L

        @JvmStatic
        @Parameterized.Parameters(
            name = "{0}",
        )
        @Suppress("unused")
        fun getParameters(): Collection<Array<Any>> =
            listOf(
                TestDataDrawingScreenTest(
                    testTagToClickOn = APPROXIMATED_SHAPE,
                    affectedTestTagAfterClick = APPROXIMATED_SHAPE_TRAILING_ICON,
                    enabledTestTags =
                        listOf(
                            APPROXIMATED_SHAPE_TRAILING_ICON,
                            FINGER_TRACED_LINES_TRAILING_ICON,
                        ),
                    disabledTestTags = emptyList(),
                ),
                TestDataDrawingScreenTest(
                    testTagToClickOn = FINGER_TRACED_LINES,
                    affectedTestTagAfterClick = FINGER_TRACED_LINES_TRAILING_ICON,
                    enabledTestTags =
                        listOf(
                            FINGER_TRACED_LINES_TRAILING_ICON,
                        ),
                    disabledTestTags =
                        listOf(
                            APPROXIMATED_SHAPE_TRAILING_ICON,
                        ),
                ),
                TestDataDrawingScreenTest(
                    testTagToClickOn = APPROXIMATED_SHAPE,
                    affectedTestTagAfterClick = APPROXIMATED_SHAPE_TRAILING_ICON,
                    enabledTestTags = emptyList(),
                    disabledTestTags =
                        listOf(
                            APPROXIMATED_SHAPE_TRAILING_ICON,
                            FINGER_TRACED_LINES_TRAILING_ICON,
                        ),
                ),
                TestDataDrawingScreenTest(
                    testTagToClickOn = FINGER_TRACED_LINES,
                    affectedTestTagAfterClick = FINGER_TRACED_LINES_TRAILING_ICON,
                    enabledTestTags =
                        listOf(
                            APPROXIMATED_SHAPE,
                        ),
                    disabledTestTags =
                        listOf(
                            FINGER_TRACED_LINES_TRAILING_ICON,
                        ),
                ),
                TestDataDrawingScreenTest(
                    testTagToClickOn = FINGER_TRACED_LINES,
                    affectedTestTagAfterClick = FINGER_TRACED_LINES_TRAILING_ICON,
                    enabledTestTags =
                        listOf(
                            APPROXIMATED_SHAPE,
                            FINGER_TRACED_LINES_TRAILING_ICON,
                        ),
                    disabledTestTags = emptyList(),
                ),
            ).map { data ->
                arrayOf(
                    data.testDescription,
                    data,
                )
            }
    }
}
