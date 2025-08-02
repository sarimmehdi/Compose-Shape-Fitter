package com.sarim.composeshapefittersampleapp.presentation

import androidx.test.ext.junit.runners.AndroidJUnit4
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
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawingScreenTest {

    @Test
    fun test() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.executeShellCommand("am start -n com.sarim.composeshapefittersampleapp/com.sarim.composeshapefittersampleapp.MainActivity")

        device.wait(Until.hasObject(By.res(TOP_BAR_COMPONENT_SETTINGS_ICON_BUTTON_TEST_TAG)), MAX_TIMEOUT)
        device.findObject(By.res(TOP_BAR_COMPONENT_SETTINGS_ICON_BUTTON_TEST_TAG)).click()

        assertThat(
            device.wait(Until.hasObject(By.res(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG)), MAX_TIMEOUT)
        ).isNotNull()
        assertThat(
            device.wait(Until.hasObject(By.res(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG)), MAX_TIMEOUT)
        ).isNotNull()
        assertThat(
            device.wait(Until.hasObject(By.res(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG)), MAX_TIMEOUT)
        ).isNotNull()
        assertThat(
            device.wait(Until.hasObject(By.res(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG)), MAX_TIMEOUT)
        ).isNotNull()
    }

    companion object {
        private const val MAX_TIMEOUT = 1000L
    }
}