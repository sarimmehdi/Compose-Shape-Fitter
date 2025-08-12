package com.sarim.test_app

import androidx.test.uiautomator.UiDevice

fun UiDevice.startApp() =
    executeShellCommand("am start -n com.sarim.test_app/com.sarim.test_app.TestActivity")
