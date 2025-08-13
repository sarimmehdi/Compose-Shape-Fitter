package com.sarim.utils

import androidx.test.uiautomator.UiDevice

fun UiDevice.startApp(
    pkg: String,
    activityPkg: String
) = executeShellCommand("am start -n $pkg/$activityPkg")
