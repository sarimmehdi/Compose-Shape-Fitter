package com.sarim.test_app.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.common.truth.Truth.assertThat
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.SETTINGS_ICON_BUTTON
import com.sarim.utils.startApp
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class DrawingScreenSnackbarTest {
//    @Test
//    fun testDismissSnackbar() {
//        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
//        device.startApp()
//
//        device.wait(Until.hasObject(By.res(SETTINGS_ICON_BUTTON)), MAX_TIMEOUT)
//        device.findObject(By.res(SETTINGS_ICON_BUTTON)).click()
//
//        testData.enabledTestTags.forEach {
//            assertThat(
//                device.wait(Until.hasObject(By.res(it)), MAX_TIMEOUT),
//            ).isTrue()
//        }
//
//        testData.disabledTestTags.forEach {
//            assertThat(
//                device.wait(Until.hasObject(By.res(it)), MAX_TIMEOUT),
//            ).isFalse()
//        }
//
//        device.findObject(By.res(testData.testTagToClickOn)).click()
//    }
}
