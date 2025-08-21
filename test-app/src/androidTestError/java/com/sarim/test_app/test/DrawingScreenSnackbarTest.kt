package com.sarim.test_app.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import java.util.regex.Pattern
import com.sarim.example_app_presentation.R
import com.sarim.utils.uiautomator.BaseUiAutomatorTestClass

@RunWith(AndroidJUnit4::class)
internal class DrawingScreenSnackbarTest :
    BaseUiAutomatorTestClass(DrawingScreenSnackbarTest::class.java.simpleName) {
    @Test
    fun testDismissSnackbar() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        startApp(pkg = "com.sarim.test_app", activityPkg = "com.sarim.test_app.TestActivity")

        assertThat(
            safeWaitForObject(
                By.text(
                    Pattern.compile("Unable to get .* because of .*")
                )
            )
        ).isTrue()
        device.findObject(By.text(context.getString(R.string.dismiss))).click()
        assertThat(
            safeWaitForObject(
                By.text(
                    Pattern.compile("Unable to get .* because of .*")
                )
            ),
        ).isFalse()
    }
}
