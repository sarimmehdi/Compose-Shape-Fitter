package com.sarim.test_app.test

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.common.truth.Truth.assertThat
import com.sarim.example_app_domain.model.Shape
import com.sarim.example_app_presentation.component.DrawerComponentTestTags.SELECTED_NAVIGATION_DRAWER_ITEM
import com.sarim.example_app_presentation.component.DrawerComponentTestTags.UNSELECTED_NAVIGATION_DRAWER_ITEM
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.OPEN_DRAWER_ICON_BUTTON
import com.sarim.utils.startApp
import com.sarim.utils.shuffleListExceptOne
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

internal data class TestDataDrawingScreenSelectedShapeTest(
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
internal class DrawingScreenSelectedShapeTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataDrawingScreenSelectedShapeTest,
) {
    @Test
    fun test() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.startApp(pkg = "com.sarim.test_app", activityPkg = "com.sarim.test_app.TestActivity")

        device.wait(Until.hasObject(By.res(OPEN_DRAWER_ICON_BUTTON)), MAX_TIMEOUT)
        device.findObject(By.res(OPEN_DRAWER_ICON_BUTTON)).click()

        testData.enabledTestTags.forEach {
            assertThat(
                device.wait(Until.hasObject(By.res(it)), MAX_TIMEOUT),
            ).isTrue()
        }

        testData.disabledTestTags.forEach {
            assertThat(
                device.wait(Until.hasObject(By.res(it)), MAX_TIMEOUT),
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
                .zipWithNext()
                .map { consecutiveShapePairs ->
                    val selectedShapeName = context.getString(consecutiveShapePairs.first.shapeStringId)
                    val notSelectedShapeName = context.getString(consecutiveShapePairs.second.shapeStringId)
                    TestDataDrawingScreenSelectedShapeTest(
                        testTagToClickOn = UNSELECTED_NAVIGATION_DRAWER_ITEM + notSelectedShapeName,
                        enabledTestTags = listOf(SELECTED_NAVIGATION_DRAWER_ITEM + selectedShapeName),
                        disabledTestTags =
                            Shape.entries.filter { it != consecutiveShapePairs.first }.map {
                                UNSELECTED_NAVIGATION_DRAWER_ITEM + context.getString(it.shapeStringId)
                            },
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
