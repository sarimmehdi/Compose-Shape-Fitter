package com.sarim.convention

import com.banno.gordon.GordonExtension
import com.sarim.convention.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class GordonConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.gordonPluginLibrary.get().group)

            extensions.configure<GordonExtension> {
                tabletShortestWidthDp.set(720)
                retryQuota.set(2)
                installTimeoutMillis.set(180_000)
                testTimeoutMillis.set(60_000)
            }

            tasks.matching { it.name == "gordon" }.configureEach {
                dependsOn("assembleDebug", "assembleDebugAndroidTest")
            }
        }
    }
}