package com.sarim.test_app.test

import androidx.test.ext.junit.runners.AndroidJUnit4
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
import com.sarim.utils.uiautomator.BaseUiAutomatorTestClass
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.collections.zipWithNext

@RunWith(AndroidJUnit4::class)
internal class DrawingScreenSelectedShapeProcessDeathTest :
    BaseUiAutomatorTestClass(DrawingScreenSelectedShapeProcessDeathTest::class.java.simpleName) {

    private data class TestData(
        val disabledTestTagsBeforeClicking: List<String>,
        val enabledTestTagsBeforeClicking: List<String>,
        val testTagToClickOn: String,
        val disabledTestTagsAfterClicking: List<String>,
        val enabledTestTagsAfterClicking: List<String>,
    )

    @Test
    @Suppress("LongMethod")
    fun test() {
        startApp(pkg = "com.sarim.test_app", activityPkg = "com.sarim.test_app.TestActivity")

        safeFindObject(By.res(OPEN_DRAWER_ICON_BUTTON)).click()
        assertThat(safeWaitForObject(By.res(LAZY_COLUMN))).isTrue()
        val lazyColumn = UiScrollable(UiSelector().resourceId(LAZY_COLUMN)).apply {
            setAsVerticalList()
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        shuffleListExceptOne(Shape.entries.toMutableList(), 0)
            .zipWithNext()
            .map { consecutiveShapePairs ->
                val selectedShapeName = context.getString(consecutiveShapePairs.first.shapeStringId)
                val notSelectedShapeName =
                    context.getString(consecutiveShapePairs.second.shapeStringId)
                TestData(
                    disabledTestTagsBeforeClicking = Shape.entries.filter { it != consecutiveShapePairs.first }
                        .map {
                            UNSELECTED_NAVIGATION_DRAWER_ITEM + context.getString(it.shapeStringId)
                        },
                    enabledTestTagsBeforeClicking = listOf(SELECTED_NAVIGATION_DRAWER_ITEM + selectedShapeName),
                    testTagToClickOn = UNSELECTED_NAVIGATION_DRAWER_ITEM + notSelectedShapeName,
                    disabledTestTagsAfterClicking = Shape.entries.filter { it != consecutiveShapePairs.second }
                        .map {
                            UNSELECTED_NAVIGATION_DRAWER_ITEM + context.getString(it.shapeStringId)
                        },
                    enabledTestTagsAfterClicking = listOf(SELECTED_NAVIGATION_DRAWER_ITEM + notSelectedShapeName),
                )
            }.forEach {
                it.disabledTestTagsBeforeClicking.forEach { disabledTestTagBeforeClicking ->
                    lazyColumn.scrollIntoView(UiSelector().resourceId(disabledTestTagBeforeClicking))
                    assertThat(
                        safeWaitForObject(By.res(disabledTestTagBeforeClicking))
                    ).isTrue()
                }
                it.enabledTestTagsBeforeClicking.forEach { enabledTestTagBeforeClicking ->
                    lazyColumn.scrollIntoView(UiSelector().resourceId(enabledTestTagBeforeClicking))
                    assertThat(
                        safeWaitForObject(By.res(enabledTestTagBeforeClicking))
                    ).isTrue()
                }

                lazyColumn.scrollIntoView(UiSelector().resourceId(it.testTagToClickOn))
                safeFindObject(By.res(it.testTagToClickOn)).click()

                device.pressHome()
                forceStopApp(
                    pkg = "com.sarim.test_app",
                    activityPkg = "com.sarim.test_app.TestActivity"
                )
                startApp(
                    pkg = "com.sarim.test_app",
                    activityPkg = "com.sarim.test_app.TestActivity"
                )

                safeFindObject(By.res(OPEN_DRAWER_ICON_BUTTON)).click()

                it.disabledTestTagsAfterClicking.forEach { disabledTestTagAfterClicking ->
                    lazyColumn.scrollIntoView(UiSelector().resourceId(disabledTestTagAfterClicking))
                    assertThat(
                        safeWaitForObject(By.res(disabledTestTagAfterClicking))
                    ).isTrue()
                }
                it.enabledTestTagsAfterClicking.forEach { enabledTestTagAfterClicking ->
                    lazyColumn.scrollIntoView(UiSelector().resourceId(enabledTestTagAfterClicking))
                    assertThat(
                        safeWaitForObject(By.res(enabledTestTagAfterClicking))
                    ).isTrue()
                }
            }
    }
}
