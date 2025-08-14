package com.sarim.test_app.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.common.truth.Truth.assertThat
import com.sarim.example_app_domain.model.Shape
import com.sarim.example_app_presentation.component.DrawerComponentTestTags.SELECTED_NAVIGATION_DRAWER_ITEM
import com.sarim.example_app_presentation.component.DrawerComponentTestTags.UNSELECTED_NAVIGATION_DRAWER_ITEM
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.OPEN_DRAWER_ICON_BUTTON
import com.sarim.utils.forceStopApp
import com.sarim.utils.shuffleListExceptOne
import com.sarim.utils.startApp
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.collections.zipWithNext

@RunWith(AndroidJUnit4::class)
internal class DrawingScreenSelectedShapeProcessDeathTest {

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
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.startApp(pkg = "com.sarim.test_app", activityPkg = "com.sarim.test_app.TestActivity")

        device.wait(Until.hasObject(By.res(OPEN_DRAWER_ICON_BUTTON)), MAX_TIMEOUT)
        device.findObject(By.res(OPEN_DRAWER_ICON_BUTTON)).click()

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
                    assertThat(
                        device.wait(
                            Until.hasObject(By.res(disabledTestTagBeforeClicking)),
                            MAX_TIMEOUT
                        ),
                    ).isTrue()
                }
                it.enabledTestTagsBeforeClicking.forEach { enabledTestTagBeforeClicking ->
                    assertThat(
                        device.wait(
                            Until.hasObject(By.res(enabledTestTagBeforeClicking)),
                            MAX_TIMEOUT
                        ),
                    ).isTrue()
                }

                device.findObject(By.res(it.testTagToClickOn)).click()

                device.pressHome()
                device.forceStopApp(
                    pkg = "com.sarim.test_app",
                    activityPkg = "com.sarim.test_app.TestActivity"
                )
                device.startApp(
                    pkg = "com.sarim.test_app",
                    activityPkg = "com.sarim.test_app.TestActivity"
                )

                device.wait(Until.hasObject(By.res(OPEN_DRAWER_ICON_BUTTON)), MAX_TIMEOUT)
                device.findObject(By.res(OPEN_DRAWER_ICON_BUTTON)).click()

                it.disabledTestTagsAfterClicking.forEach { disabledTestTagAfterClicking ->
                    assertThat(
                        device.wait(
                            Until.hasObject(By.res(disabledTestTagAfterClicking)),
                            MAX_TIMEOUT
                        ),
                    ).isTrue()
                }
                it.enabledTestTagsAfterClicking.forEach { enabledTestTagAfterClicking ->
                    assertThat(
                        device.wait(
                            Until.hasObject(By.res(enabledTestTagAfterClicking)),
                            MAX_TIMEOUT
                        ),
                    ).isTrue()
                }
            }
    }

    companion object {
        private const val MAX_TIMEOUT = 5000L
    }
}
