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
    alias(libs.plugins.sonarPlugin)
    alias(libs.plugins.paparazziPlugin) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
    alias(libs.plugins.conventionPluginJacocoId)
}

val sonarModules = setOf(
    "example-app",
    "example-app-data",
    "example-app-domain",
    "example-app-presentation",
)
val nonSonarModules = subprojects
    .filter { it.name !in sonarModules }
    .map { it.name }
val sonarExclusionString =
    nonSonarModules.joinToString(separator = ",") { moduleName -> "**/$moduleName/**" }

sonar {
    properties {
        property("sonar.coverage.exclusions", sonarExclusionString)
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${project.rootDir}/build/reports/jacoco/jacocoAggregatedReport/jacocoAggregatedReport.xml"
        )
    }
}

subprojects {
    pluginManager.apply(rootProject.libs.plugins.ktlintPlugin.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.detektPlugin.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.spotlessPlugin.get().pluginId)

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