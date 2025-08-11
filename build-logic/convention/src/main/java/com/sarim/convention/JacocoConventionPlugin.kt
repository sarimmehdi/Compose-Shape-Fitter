package com.sarim.convention

import com.android.build.api.dsl.LibraryExtension
import com.sarim.convention.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

class JacocoConventionPlugin : Plugin<Project> {
    private val jacocoEnabledModules = listOf(
        ":example-app:example-app-data",
        ":example-app:example-app-domain",
        ":example-app:example-app-presentation"
    )

    private val fileFilter =
        listOf(
            "**/R.class",
            "**/R$*.class",
            "**/Manifest*.*",
            "**/BuildConfig.class",
            "**/*Test.class",
            "**/*Test$*.class",
            "**/*Tests.class",
            "**/*Tests$*.class",
            "**/*UnitTest.class",
            "**/*UnitTest$*.class",
            "**/*InstrumentedTest.class",
            "**/*InstrumentedTest$*.class",
            "**/*Spec.class",
            "**/*Spec$*.class",
            "android/**/*.*",
            "androidx/**/*.*",
            "com/android/**/*.*",
            "com/google/android/material/**/*.*",
            "kotlinx/**/*.*",
            "kotlin/coroutines/**/*.*",
            "java/**/*.*",
            "javax/**/*.*",
            "org/intellij/lang/annotations/**/*.*",
            "org/jetbrains/annotations/**/*.*",
            "**/*ComposableSingletons*.*",
            "**/*Kt$ années*.*",
            "**/*Kt$*.class",
            "**/databinding/*Binding.class",
            "androidx/databinding/**/*.*",
            "**/*\$ViewInjector*.*",
            "**/*\$ViewBinder*.*",
            "**/*Module.class",
            "**/*Module$*.class",
            "**/$*$.class",
            "timber/**/*.*",
            "com/jakewharton/timber/**/*.*",
            "org/mockito/**/*.*",
            "io/mockk/**/*.*",
            "**/*\$MockitoMock*$.class",
            "**/*\$MockK*.class",
        )

    override fun apply(target: Project) {
        with(target) {
            val jacocoToolVersion = libs.versions.jacocoVersion.get()
            if (target == target.rootProject) {
                configureAggregatedReport(jacocoToolVersion)
            } else if (target.path in jacocoEnabledModules) {
                configureJacocoForSubproject(jacocoToolVersion)
            } else {
                target.logger.error(
                    "JacocoConventionPlugin: Skipping JaCoCo configuration for ${target.path} " +
                            "as it's not in the jacocoEnabledModules list: $jacocoEnabledModules"
                )
            }
        }
    }

    private fun Project.configureJacocoForSubproject(toolVersion: String) {
        pluginManager.apply("jacoco")
        extensions.configure<JacocoPluginExtension> {
            this.toolVersion = toolVersion
        }
        extensions.configure<LibraryExtension> {
            buildTypes {
                debug {
                    enableUnitTestCoverage = true
                    enableAndroidTestCoverage = true
                }
            }
        }
        logger.lifecycle("JaCoCo configured for module: ${project.path}")
    }

    private fun Project.configureAggregatedReport(toolVersion: String) {
        pluginManager.apply("jacoco")
        extensions.configure<JacocoPluginExtension> {
            this.toolVersion = toolVersion
        }

        tasks.register<JacocoReport>("jacocoAggregatedReport") {
            group = "verification"
            description = "Generates JaCoCo code coverage reports for the debug build."

            val projectsToInclude = jacocoEnabledModules
                .mapNotNull { project.findProject(it) }

            if (projectsToInclude.isNotEmpty()) {
                dependsOn(
                    ":example-app:example-app-data:testDebugUnitTest",
                    ":example-app:example-app-domain:testDebugUnitTest",
                    ":example-app:example-app-presentation:testDebugUnitTest",
                    ":example-app:example-app-data:createDebugCoverageReport",
                    ":example-app:example-app-domain:createDebugCoverageReport",
                    ":example-app:example-app-presentation:createDebugCoverageReport"
                )

                reports {
                    html.required.set(true)
                    xml.required.set(true)
                    csv.required.set(false)
                }

                val mainSrc = "${project.projectDir}/src/main/java"
                val kotlinSrc = "${project.projectDir}/src/main/kotlin"
                sourceDirectories.setFrom(
                    rootProject.files(mainSrc, kotlinSrc).filter { it.exists() })

                val kotlinClassesDirProvider =
                    rootProject.layout.buildDirectory.dir("tmp/kotlin-classes/debug")
                classDirectories.from(
                    kotlinClassesDirProvider.map { dir ->
                        project.fileTree(dir) {
                            exclude(fileFilter)
                        }
                    },
                )
                executionData.setFrom(
                    fileTree(layout.buildDirectory.dir("outputs/unit_test_code_coverage/debugUnitTest")) {
                        include("*.exec")
                    },
                    fileTree(layout.buildDirectory.dir("outputs/code_coverage/debugAndroidTest/connected/")) {
                        include("**/*.ec")
                    },
                )
            } else {
                enabled = false
                project.logger.error("JaCoCo: No specified modules found for coverage report.")
            }
        }
    }
}