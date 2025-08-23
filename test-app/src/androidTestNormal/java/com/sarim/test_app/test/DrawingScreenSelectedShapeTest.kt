package com.sarim.test_app.test

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import com.google.common.truth.Truth.assertThat
import com.sarim.example_app_domain.model.Shape
import com.sarim.example_app_presentation.component.DrawerComponentTestTags.LAZY_COLUMN
import com.sarim.example_app_presentation.component.DrawerComponentTestTags.SELECTED_NAVIGATION_DRAWER_ITEM
import com.sarim.example_app_presentation.component.DrawerComponentTestTags.UNSELECTED_NAVIGATION_DRAWER_ITEM
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.OPEN_DRAWER_ICON_BUTTON
import com.sarim.utils.list.shuffleListExceptOne
import com.sarim.utils.log.LogType
import com.sarim.utils.log.log
import com.sarim.utils.uiautomator.BaseUiAutomatorTestClass
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
) : BaseUiAutomatorTestClass(
    pkg = "com.sarim.test_app",
    activityPkg = "com.sarim.test_app.TestActivity",
    logTag = DrawingScreenSelectedShapeTest::class.java.simpleName
) {
    @Test
    fun test() {
        log(
            tag = DrawingScreenSelectedShapeTest::class.java.simpleName,
            messageBuilder = { testData.testDescription },
            logType = LogType.INFO,
            shouldLog = true
        )

        startApp()

        safeFindObject(By.res(OPEN_DRAWER_ICON_BUTTON)).click()
        assertThat(safeWaitForObject(By.res(LAZY_COLUMN))).isTrue()
        val lazyColumn = UiScrollable(UiSelector().resourceId(LAZY_COLUMN)).apply {
            setAsVerticalList()
        }

        testData.enabledTestTags.forEach {
            lazyColumn.scrollIntoView(UiSelector().resourceId(it))
            assertThat(safeWaitForObject(By.res(it))).isTrue()
        }

        testData.disabledTestTags.forEach {
            lazyColumn.scrollIntoView(UiSelector().resourceId(it))
            assertThat(safeWaitForObject(By.res(it))).isTrue()
        }

        lazyColumn.scrollIntoView(UiSelector().resourceId(testData.testTagToClickOn))
        safeFindObject(By.res(testData.testTagToClickOn)).click()
    }

    companion object {

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
                }.mapIndexed { i, data ->
                    arrayOf(
                        i.toString(),
                        data,
                    )
                }
        }
    }
}
