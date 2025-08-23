package com.sarim.test_app

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

@Suppress("UNUSED_PARAMETER")
class InstrumentationTestNormalRunner : AndroidJUnitRunner() {
    override fun newApplication(
        classLoader: ClassLoader?,
        className: String?,
        context: Context?,
    ): Application = super.newApplication(classLoader, TestNormalApplication::class.java.name, context)
}
