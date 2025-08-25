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
    private val jacocoIncludePatterns = listOf(
        "**/*Dto.class",
        "**/model/**/*.class",
        "**/*Repository.class",
        "**/*UseCase.class",
        "**/*Screen.class",
        "**/*ScreenState.class",
        "**/*ScreenToViewModelEvents.class",
        "**/*UseCases.class",
        "**/*ViewModel.class",
        "**/*Component.class"
    )

    override fun apply(target: Project) {
        with(target) {
            val jacocoToolVersion = libs.versions.jacocoVersion.get()
            if (target == target.rootProject) {
                configureAggregatedReport(jacocoToolVersion)
            } else {
                configureJacocoForSubproject(jacocoToolVersion)
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

            val projectsToInclude = subprojects.filter { subproject ->
                subproject.plugins.hasPlugin("jacoco")
            }

            if (projectsToInclude.isNotEmpty()) {
                logger.lifecycle("JacocoAggregatedReport: Will aggregate coverage for projects: " +
                        "${projectsToInclude.map { it.path }}")
                reports {
                    html.required.set(true)
                    xml.required.set(true)
                    csv.required.set(false)
                }

                sourceDirectories.setFrom(files(projectsToInclude.map { module ->
                    listOf(
                        module.layout.projectDirectory.dir("src/main/java"),
                        module.layout.projectDirectory.dir("src/main/kotlin")
                    )
                }))

                classDirectories.setFrom(
                    projectsToInclude.map { module ->
                        val kotlinClassesDir = module.layout.buildDirectory.dir("tmp/kotlin-classes/debug")
                        module.fileTree(kotlinClassesDir) {
                            include(jacocoIncludePatterns)
                        }
                    }
                )
                executionData.setFrom(
                    projectsToInclude.flatMap { module ->
                        val unitTestExecFile = module.layout.buildDirectory.file(
                            "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
                        ).orNull?.asFile

                        val filesToInclude = mutableListOf<Any>()
                        if (unitTestExecFile?.exists() == true) {
                            filesToInclude.add(unitTestExecFile)
                        }
                        filesToInclude
                    }.filter {
                        when (it) {
                            is java.io.File -> it.exists()
                            is org.gradle.api.file.FileTree -> !it.isEmpty
                            else -> false
                        }
                    }
                )
            } else {
                enabled = false
                project.logger.error("JaCoCo: No specified modules found for coverage report.")
            }
        }
    }
}