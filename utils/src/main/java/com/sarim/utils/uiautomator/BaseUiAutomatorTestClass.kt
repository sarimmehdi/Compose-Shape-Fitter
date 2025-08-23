package com.sarim.utils.uiautomator

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.sarim.utils.log.LogType
import com.sarim.utils.log.log
import org.junit.After
import org.junit.Before

open class BaseUiAutomatorTestClass(
    private val pkg: String,
    private val activityPkg: String,
    private val logTag: String,
) {

    protected lateinit var device: UiDevice

    @Before
    open fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)
        device.executeShellCommand("settings put global window_animation_scale 0")
        device.executeShellCommand("settings put global transition_animation_scale 0")
        device.executeShellCommand("settings put global animator_duration_scale 0")
    }

    @After
    open fun tearDown() {
        device.executeShellCommand("settings put global window_animation_scale 1")
        device.executeShellCommand("settings put global transition_animation_scale 1")
        device.executeShellCommand("settings put global animator_duration_scale 1")
    }

    fun startApp() = device.executeShellCommand("am start -n $pkg/$activityPkg")

    fun safeFindObject(selector: BySelector): UiObject2 {
        val obj = device.wait(Until.findObject(selector), MAX_TIMEOUT)
        if (obj == null) {
            dumpVisibleHierarchy()
            log(
                logTag,
                { "Element with selector [$selector] not found within ${MAX_TIMEOUT}ms" },
                LogType.ERROR,
                shouldLog = true
            )
            throw AssertionError("Element with selector [$selector] not found within ${MAX_TIMEOUT}ms")
        }
        return obj
    }

    fun safeWaitForObject(selector: BySelector): Boolean {
        val found = device.wait(Until.hasObject(selector), MAX_TIMEOUT)
        if (!found) {
            dumpVisibleHierarchy()
            log(
                logTag,
                { "Object with selector [$selector] not found within ${MAX_TIMEOUT}ms" },
                LogType.ERROR,
                shouldLog = true
            )
        }
        return found
    }

    private fun dumpVisibleHierarchy() {
        val roots = device.findObjects(By.pkg(device.currentPackageName))
        log(logTag, { "====== UI HIERARCHY DUMP ======" }, LogType.ERROR, shouldLog = true)
        for (root in roots) {
            dumpNode(root, 0)
        }
        log(logTag, { "===============================" }, LogType.ERROR, shouldLog = true)
    }

    private fun dumpNode(node: UiObject2, indent: Int) {
        val indentStr = " ".repeat(indent * 2)
        log(
            logTag,
            {
                "$indentStr id=${node.resourceName} " +
                        "desc=${node.contentDescription} " +
                        "text=${node.text}"
            },
            LogType.ERROR,
            shouldLog = true
        )
        for (child in node.children) {
            dumpNode(child, indent + 1)
        }
    }

    companion object {
        private const val MAX_TIMEOUT = 10_000L
    }
}