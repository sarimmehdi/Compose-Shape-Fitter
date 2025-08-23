import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import org.sonarqube.gradle.SonarExtension

plugins {
    alias(libs.plugins.androidApplicationPlugin) apply false
    alias(libs.plugins.kotlinAndroidPlugin) apply false
    alias(libs.plugins.kotlinComposePlugin) apply false
    alias(libs.plugins.androidLibraryPlugin) apply false
    alias(libs.plugins.gordonPlugin) apply false
    alias(libs.plugins.ktlintPlugin) apply false
    alias(libs.plugins.detektPlugin) apply false
    alias(libs.plugins.spotlessPlugin) apply false
    alias(libs.plugins.sonarPlugin) apply false
    alias(libs.plugins.paparazziPlugin) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
    alias(libs.plugins.conventionPluginJacocoId)
}

subprojects {
    pluginManager.apply(rootProject.libs.plugins.ktlintPlugin.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.detektPlugin.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.spotlessPlugin.get().pluginId)

    plugins.withId(rootProject.libs.plugins.sonarPlugin.get().pluginId) {
        configure<SonarExtension> {
            properties {
                property("sonar.host.url", "http://localhost:9000")
                property("sonar.token", "sqp_b969d7de9028533c76c55fbbc0083bf7c7cbfa1a")
                property("sonar.projectKey", "Compose-Shape-Fitter")
                property("sonar.projectName", "Compose Shape Fitter")
                property(
                    "sonar.coverage.jacoco.xmlReportPaths",
                    "${project.rootDir}/build/reports/jacoco/jacocoAggregatedReport/jacocoAggregatedReport.xml"
                )
//                property("sonar.kotlin.detekt.reportPaths", "build/reports/detekt/detekt.xml")
//                property("sonar.kotlin.ktlint.reportPaths", "build/reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.xml")
//                property("sonar.androidLint.reportPaths", "build/reports/lint-results-debug.xml")
//                property("sonar.sources", "src/main/java")
//                property("sonar.tests", "src/test/java,src/androidTest/java")
//                property("sonar.sourceEncoding", "UTF-8")
            }
        }
    }

    configure<DetektExtension> {
        parallel = true
        config.setFrom("${project.rootDir}/config/detekt/detekt.yml")
    }

    configure<KtlintExtension> {
        android = true
        ignoreFailures = false
        reporters {
            reporter(ReporterType.PLAIN)
            reporter(ReporterType.CHECKSTYLE)
            reporter(ReporterType.SARIF)
        }
    }

    configure<SpotlessExtension> {
        kotlin {
            target("src/**/*.kt")
            ktlint().setEditorConfigPath("${project.rootDir}/.editorconfig")
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}