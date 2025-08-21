package com.sarim.test_app.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import com.google.common.truth.Truth.assertThat
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.APPROXIMATED_SHAPE
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.APPROXIMATED_SHAPE_TRAILING_ICON
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.FINGER_TRACED_LINES
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.FINGER_TRACED_LINES_TRAILING_ICON
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.SETTINGS_ICON_BUTTON
import com.sarim.utils.uiautomator.BaseUiAutomatorTestClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class DrawingScreenSettingsProcessDeathTest
    : BaseUiAutomatorTestClass(DrawingScreenSettingsProcessDeathTest::class.java.simpleName) {

    private data class TestData(
        val testTagToVerifyBeforeClicking1: Pair<String, Boolean>,
        val testTagToVerifyBeforeClicking2: Pair<String, Boolean>,
        val testTagToClickOn: String,
        val testTagToVerifyAfterClicking1: Pair<String, Boolean>,
        val testTagToVerifyAfterClicking2: Pair<String, Boolean>,
    )

    @Test
    @Suppress("LongMethod")
    fun test() {
        startApp(pkg = "com.sarim.test_app", activityPkg = "com.sarim.test_app.TestActivity")

        safeFindObject(By.res(SETTINGS_ICON_BUTTON)).click()

        listOf(
            TestData(
                testTagToVerifyBeforeClicking1 = Pair(APPROXIMATED_SHAPE_TRAILING_ICON, true),
                testTagToVerifyBeforeClicking2 = Pair(FINGER_TRACED_LINES_TRAILING_ICON, true),
                testTagToClickOn = APPROXIMATED_SHAPE,
                testTagToVerifyAfterClicking1 = Pair(APPROXIMATED_SHAPE_TRAILING_ICON, false),
                testTagToVerifyAfterClicking2 = Pair(FINGER_TRACED_LINES_TRAILING_ICON, true)
            ),
            TestData(
                testTagToVerifyBeforeClicking1 = Pair(APPROXIMATED_SHAPE_TRAILING_ICON, false),
                testTagToVerifyBeforeClicking2 = Pair(FINGER_TRACED_LINES_TRAILING_ICON, true),
                testTagToClickOn = FINGER_TRACED_LINES,
                testTagToVerifyAfterClicking1 = Pair(APPROXIMATED_SHAPE_TRAILING_ICON, false),
                testTagToVerifyAfterClicking2 = Pair(FINGER_TRACED_LINES_TRAILING_ICON, false)
            ),
            TestData(
                testTagToVerifyBeforeClicking1 = Pair(APPROXIMATED_SHAPE_TRAILING_ICON, false),
                testTagToVerifyBeforeClicking2 = Pair(FINGER_TRACED_LINES_TRAILING_ICON, false),
                testTagToClickOn = FINGER_TRACED_LINES,
                testTagToVerifyAfterClicking1 = Pair(APPROXIMATED_SHAPE_TRAILING_ICON, false),
                testTagToVerifyAfterClicking2 = Pair(FINGER_TRACED_LINES_TRAILING_ICON, true)
            ),
            TestData(
                testTagToVerifyBeforeClicking1 = Pair(APPROXIMATED_SHAPE_TRAILING_ICON, false),
                testTagToVerifyBeforeClicking2 = Pair(FINGER_TRACED_LINES_TRAILING_ICON, true),
                testTagToClickOn = APPROXIMATED_SHAPE,
                testTagToVerifyAfterClicking1 = Pair(APPROXIMATED_SHAPE_TRAILING_ICON, true),
                testTagToVerifyAfterClicking2 = Pair(FINGER_TRACED_LINES_TRAILING_ICON, true)
            )
        ).forEach {
            assertThat(
                safeWaitForObject(By.res(it.testTagToVerifyBeforeClicking1.first)),
            ).apply {
                if (it.testTagToVerifyBeforeClicking1.second) {
                    isTrue()
                } else {
                    isFalse()
                }
            }
            assertThat(
                safeWaitForObject(By.res(it.testTagToVerifyBeforeClicking2.first)),
            ).apply {
                if (it.testTagToVerifyBeforeClicking2.second) {
                    isTrue()
                } else {
                    isFalse()
                }
            }
            safeFindObject(By.res(it.testTagToClickOn)).click()

            device.pressHome()
            forceStopApp(pkg = "com.sarim.test_app", activityPkg = "com.sarim.test_app.TestActivity")
            startApp(pkg = "com.sarim.test_app", activityPkg = "com.sarim.test_app.TestActivity")

            assertThat(
                safeWaitForObject(By.res(it.testTagToVerifyAfterClicking1.first)),
            ).apply {
                if (it.testTagToVerifyAfterClicking1.second) {
                    isTrue()
                } else {
                    isFalse()
                }
            }
            assertThat(
                safeWaitForObject(By.res(it.testTagToVerifyAfterClicking2.first)),
            ).apply {
                if (it.testTagToVerifyAfterClicking2.second) {
                    isTrue()
                } else {
                    isFalse()
                }
            }
        }
    }
}
