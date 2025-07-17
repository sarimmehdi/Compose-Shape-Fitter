import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.androidApplicationPlugin)
    alias(libs.plugins.kotlinAndroidPlugin)
    alias(libs.plugins.kotlinComposePlugin)
    alias(libs.plugins.ktlintPlugin)
}

android {
    namespace = "com.sarim.composeshapefittersampleapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sarim.composeshapefittersampleapp"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
        }
    }
    buildFeatures {
        compose = true
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
    implementation(platform(libs.androidxComposeBomLibrary))
    implementation(libs.androidxUiLibrary)
    implementation(libs.androidxUiGraphicsLibrary)
    implementation(libs.androidxUiToolingPreviewLibrary)
    implementation(libs.androidxMaterial3Library)
    debugImplementation(libs.androidxUiToolingLibrary)
    debugImplementation(libs.androidxUiTestManifestLibrary)
    implementation(project(":compose-shape-fitter"))
}
