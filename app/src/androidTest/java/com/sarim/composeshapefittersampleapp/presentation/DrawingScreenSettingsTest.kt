package com.sarim.composeshapefittersampleapp.presentation

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.common.truth.Truth.assertThat
import com.sarim.composeshapefittersampleapp.presentation.component.TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG
import com.sarim.composeshapefittersampleapp.presentation.component.TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG
import com.sarim.composeshapefittersampleapp.presentation.component.TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG
import com.sarim.composeshapefittersampleapp.presentation.component.TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG
import com.sarim.composeshapefittersampleapp.presentation.component.TOP_BAR_COMPONENT_SETTINGS_ICON_BUTTON_TEST_TAG
import com.sarim.composeshapefittersampleapp.startApp
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

        device.wait(Until.hasObject(By.res(TOP_BAR_COMPONENT_SETTINGS_ICON_BUTTON_TEST_TAG)), MAX_TIMEOUT)
        device.findObject(By.res(TOP_BAR_COMPONENT_SETTINGS_ICON_BUTTON_TEST_TAG)).click()

        testData.enabledTestTags.forEach {
            assertThat(
                device.wait(Until.hasObject(By.res(it)), MAX_TIMEOUT)
            ).isTrue()
        }

        testData.disabledTestTags.forEach {
            assertThat(
                device.wait(Until.hasObject(By.res(it)), MAX_TIMEOUT)
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
        fun getParameters(): Collection<Array<Any>> {
            return listOf(
                TestDataDrawingScreenTest(
                    testTagToClickOn = TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG,
                    affectedTestTagAfterClick = TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG,
                    enabledTestTags = listOf(
                        TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG,
                        TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG
                    ),
                    disabledTestTags = emptyList()
                ),
                TestDataDrawingScreenTest(
                    testTagToClickOn = TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG,
                    affectedTestTagAfterClick = TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG,
                    enabledTestTags = listOf(
                        TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG
                    ),
                    disabledTestTags = listOf(
                        TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG
                    )
                ),
                TestDataDrawingScreenTest(
                    testTagToClickOn = TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG,
                    affectedTestTagAfterClick = TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG,
                    enabledTestTags = emptyList(),
                    disabledTestTags = listOf(
                        TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG,
                        TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG
                    )
                ),
                TestDataDrawingScreenTest(
                    testTagToClickOn = TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG,
                    affectedTestTagAfterClick = TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG,
                    enabledTestTags = listOf(
                        TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG
                    ),
                    disabledTestTags = listOf(
                        TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG
                    )
                ),
                TestDataDrawingScreenTest(
                    testTagToClickOn = TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG,
                    affectedTestTagAfterClick = TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG,
                    enabledTestTags = listOf(
                        TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG,
                        TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG
                    ),
                    disabledTestTags = emptyList()
                ),
            ).map { data ->
                arrayOf(
                    data.testDescription,
                    data,
                )
            }
        }
    }
}