package com.sarim.composeshapefittersampleapp

import androidx.test.uiautomator.UiDevice

fun UiDevice.startApp() = executeShellCommand("am start -n com.sarim.composeshapefittersampleapp/com.sarim.composeshapefittersampleapp.MainActivity")