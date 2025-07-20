import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.androidApplicationPlugin)
    alias(libs.plugins.kotlinAndroidPlugin)
    alias(libs.plugins.kotlinComposePlugin)
    alias(libs.plugins.kotlinSerializationPlugin)
    alias(libs.plugins.ktlintPlugin)
    alias(libs.plugins.detektPlugin)
    id("kotlin-parcelize")
}

android {
    namespace = "com.sarim.composeshapefittersampleapp"
    //noinspection GradleDependency
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sarim.composeshapefittersampleapp"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all { test ->
                // Target all unit tests
                test.testLogging {
                    // Show standard out and standard error of the test JVM(s).
                    showStandardStreams = true

                    events("started", "passed", "skipped", "failed", "standard_out", "standard_error")
                    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                }
            }
        }
    }
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

dependencies {

    implementation(libs.androidxCoreKtxLibrary)
    implementation(libs.androidxLifecycleRuntimeKtxLibrary)
    implementation(libs.androidxActivityComposeLibrary)
    implementation(platform(libs.koinBomLibrary))
    implementation(libs.bundles.koinBundle)
    implementation(platform(libs.androidxComposeBomLibrary))
    implementation(libs.bundles.composeImplementationBundle)
    implementation(libs.bundles.dataStorageBundle)
    debugImplementation(libs.bundles.composeDebugImplementationBundle)
    testImplementation(platform(libs.androidxComposeBomLibrary))
    testImplementation(libs.composeJunit4Library)
    testImplementation(libs.bundles.testBundle)
    implementation(project(":compose-shape-fitter"))
}
