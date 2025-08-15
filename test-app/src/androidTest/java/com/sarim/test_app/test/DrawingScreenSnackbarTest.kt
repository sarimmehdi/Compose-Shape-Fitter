package com.sarim.test_app.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.common.truth.Truth.assertThat
import com.sarim.utils.startApp
import org.junit.Test
import org.junit.runner.RunWith
import java.util.regex.Pattern
import com.sarim.example_app_presentation.R

@RunWith(AndroidJUnit4::class)
internal class DrawingScreenSnackbarTest {
    @Test
    fun testDismissSnackbar() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.startApp(pkg = "com.sarim.test_app", activityPkg = "com.sarim.test_app.TestActivity")

        assertThat(
            device.wait(
                Until.hasObject(By.text(
                    Pattern.compile("Unable to get .* because of .*")
                )),
                MAX_TIMEOUT
            ),
        ).isTrue()
        device.findObject(By.text(context.getString(R.string.dismiss))).click()
        assertThat(
            device.wait(
                Until.hasObject(By.text(
                    Pattern.compile("Unable to get .* because of .*")
                )),
                MAX_TIMEOUT
            ),
        ).isFalse()
    }

    companion object {
        private const val MAX_TIMEOUT = 5000L
    }
}
