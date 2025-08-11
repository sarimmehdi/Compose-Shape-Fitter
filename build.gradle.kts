import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.sonarqube.gradle.SonarExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    alias(libs.plugins.androidApplicationPlugin) apply false
    alias(libs.plugins.kotlinAndroidPlugin) apply false
    alias(libs.plugins.kotlinComposePlugin) apply false
    alias(libs.plugins.androidLibraryPlugin) apply false
    alias(libs.plugins.ktlintPlugin) apply false
    alias(libs.plugins.detektPlugin) apply false
    alias(libs.plugins.spotlessPlugin) apply false
    alias(libs.plugins.sonarPlugin) apply false
    alias(libs.plugins.paparazziPlugin) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
}

subprojects {
    pluginManager.apply(rootProject.libs.plugins.ktlintPlugin.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.detektPlugin.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.spotlessPlugin.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.sonarPlugin.get().pluginId)

    if (project.name != ":compose-shape-fitter") {
        configure<SonarExtension> {
            properties {
                property("sonar.host.url", "http://localhost:9000")
                property("sonar.token", "sqp_f725693eadebef9986eee82750530feeb63fff7d")
                property("sonar.projectKey", "Compose-Shape-Fitter")
                property("sonar.projectName", "Compose Shape Fitter")
                property("sonar.qualitygate.wait", true)
                property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
                property("sonar.kotlin.detekt.reportPaths", "build/reports/detekt/detekt.xml")
                property("sonar.kotlin.ktlint.reportPaths", "build/reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.xml")
                property("sonar.androidLint.reportPaths", "build/reports/lint-results-debug.xml")
                property("sonar.sources", "src/main/java")
                property("sonar.tests", "src/test/java,src/androidTest/java")
                property("sonar.sourceEncoding", "UTF-8")
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