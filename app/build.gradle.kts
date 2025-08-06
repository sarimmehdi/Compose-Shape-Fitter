import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.androidApplicationPlugin)
    alias(libs.plugins.kotlinAndroidPlugin)
    alias(libs.plugins.kotlinComposePlugin)
    alias(libs.plugins.kotlinSerializationPlugin)
    alias(libs.plugins.ktlintPlugin)
    alias(libs.plugins.detektPlugin)
    alias(libs.plugins.spotlessPlugin)
    alias(libs.plugins.sonarPlugin)
    id("kotlin-parcelize")
    id("jacoco")
}

sonarqube {
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

android {
    namespace = "com.sarim.composeshapefittersampleapp"
    //noinspection GradleDependency
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sarim.composeshapefittersampleapp"
        minSdk = 26
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.sarim.composeshapefittersampleapp.InstrumentationTestRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
            all { test ->
                test.testLogging {
                    showStandardStreams = true

                    events("started", "passed", "skipped", "failed", "standard_out", "standard_error")
                    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                }
            }
        }
    }
    packaging {
        resources {
            pickFirsts.add("META-INF/AL2.0")
            pickFirsts.add("META-INF/LGPL2.1")
            pickFirsts.add("META-INF/LICENSE.md")
            pickFirsts.add("META-INF/LICENSE-notice.md")
        }
    }
}

jacoco {
    toolVersion = libs.versions.jacocoVersion.get()
}

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "verification"
    description = "Generates JaCoCo code coverage reports for the debug build."

    dependsOn("testDebugUnitTest", "createDebugCoverageReport")

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }

    val mainSrc = "${project.projectDir}/src/main/java"
    val kotlinSrc = "${project.projectDir}/src/main/kotlin"
    sourceDirectories.setFrom(files(mainSrc, kotlinSrc).filter { it.exists() })
    val fileFilter =
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

    val kotlinClassesDirProvider = layout.buildDirectory.dir("tmp/kotlin-classes/debug")
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
}

detekt {
    parallel = true
    config.setFrom("${project.rootDir}/config/detekt/detekt.yml")
}

ktlint {
    android = true
    ignoreFailures = false
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.SARIF)
    }
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint().setEditorConfigPath("${project.rootDir}/.editorconfig")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

dependencies {

    implementation(libs.androidxCoreKtxLibrary)
    implementation(libs.androidxLifecycleRuntimeKtxLibrary)
    implementation(libs.androidxActivityComposeLibrary)
    implementation(platform(libs.koinBomLibrary))
    implementation(libs.bundles.koinBundle)
    implementation(platform(libs.androidxComposeBomLibrary))
    implementation(libs.bundles.composeImplementationBundle)
    implementation(libs.bundles.dataStorageBundle)
    implementation(kotlin("reflect"))
    implementation(project(":compose-shape-fitter"))

    debugImplementation(libs.bundles.composeDebugImplementationBundle)

    testImplementation(platform(libs.androidxComposeBomLibrary))
    testImplementation(libs.composeJunit4Library)
    testImplementation(libs.bundles.testBundle)

    androidTestImplementation(platform(libs.androidxComposeBomLibrary))
    androidTestImplementation(libs.composeJunit4Library)
    androidTestImplementation(libs.bundles.androidTestBundle)
}
