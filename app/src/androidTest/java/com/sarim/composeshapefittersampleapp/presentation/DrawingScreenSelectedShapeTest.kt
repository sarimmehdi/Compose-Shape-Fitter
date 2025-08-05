package com.sarim.composeshapefittersampleapp.presentation

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.common.truth.Truth.assertThat
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.presentation.component.DRAWER_COMPONENT_SELECTED_NAVIGATION_DRAWER_ITEM_TEST_TAG_NOT_SELECTED_FOR_
import com.sarim.composeshapefittersampleapp.presentation.component.DRAWER_COMPONENT_SELECTED_NAVIGATION_DRAWER_ITEM_TEST_TAG_SELECTED_FOR_
import com.sarim.composeshapefittersampleapp.presentation.component.TOP_BAR_COMPONENT_OPEN_DRAWER_ICON_BUTTON_TEST_TAG
import com.sarim.composeshapefittersampleapp.startApp
import com.sarim.composeshapefittersampleapp.utils.shuffleListExceptOne
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

data class TestDataDrawingScreenSelectedShapeTest(
    val testTagToClickOn: String,
    val enabledTestTags: List<String>,
    val disabledTestTags: List<String>,
) {
    val testDescription =
        "when input node with $testTagToClickOn is clicked, \n" +
                "these test tags must be enabled before the click action: $enabledTestTags, and \n" +
                "these test tags must be disabled before the click action: $disabledTestTags"
}

@RunWith(Parameterized::class)
class DrawingScreenSelectedShapeTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataDrawingScreenSelectedShapeTest,
) {

    @Test
    fun test() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.startApp()

        device.wait(Until.hasObject(By.res(TOP_BAR_COMPONENT_OPEN_DRAWER_ICON_BUTTON_TEST_TAG)), MAX_TIMEOUT)
        device.findObject(By.res(TOP_BAR_COMPONENT_OPEN_DRAWER_ICON_BUTTON_TEST_TAG)).click()

        testData.enabledTestTags.forEach {
            assertThat(
                device.wait(Until.hasObject(By.res(it)), MAX_TIMEOUT)
            ).isTrue()
        }

        testData.disabledTestTags.forEach {
            assertThat(
                device.wait(Until.hasObject(By.res(it)), MAX_TIMEOUT)
            ).isTrue()
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
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            return shuffleListExceptOne(Shape.entries.toMutableList(), 0)
                .zipWithNext().map { consecutiveShapePairs ->
                    val selectedShapeName = context.getString(consecutiveShapePairs.first.shapeStringId)
                    val notSelectedShapeName = context.getString(consecutiveShapePairs.second.shapeStringId)
                    TestDataDrawingScreenSelectedShapeTest(
                        testTagToClickOn = DRAWER_COMPONENT_SELECTED_NAVIGATION_DRAWER_ITEM_TEST_TAG_NOT_SELECTED_FOR_ + notSelectedShapeName,
                        enabledTestTags = listOf(DRAWER_COMPONENT_SELECTED_NAVIGATION_DRAWER_ITEM_TEST_TAG_SELECTED_FOR_ + selectedShapeName),
                        disabledTestTags = Shape.entries.filter { it != consecutiveShapePairs.first }.map {
                            DRAWER_COMPONENT_SELECTED_NAVIGATION_DRAWER_ITEM_TEST_TAG_NOT_SELECTED_FOR_ + context.getString(it.shapeStringId)
                        }
                    )
                }.map { data ->
                    arrayOf(
                        data.testDescription,
                        data,
                    )
                }
        }
    }
}