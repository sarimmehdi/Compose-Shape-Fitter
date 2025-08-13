package com.sarim.test_app

import androidx.test.uiautomator.UiDevice

internal fun UiDevice.startApp() =
    executeShellCommand("am start -n com.sarim.test_app/com.sarim.test_app.TestActivity")
