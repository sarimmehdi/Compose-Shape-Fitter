package com.sarim.example_app_presentation

import androidx.test.uiautomator.UiDevice

fun UiDevice.startApp() =
    executeShellCommand("am start -n com.sarim.composeshapefittersampleapp/com.sarim.composeshapefittersampleapp.MainActivity")
