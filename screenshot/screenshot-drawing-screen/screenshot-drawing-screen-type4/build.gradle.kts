plugins {
    alias(libs.plugins.androidLibraryPlugin)
    alias(libs.plugins.kotlinAndroidPlugin)
    alias(libs.plugins.kotlinComposePlugin)
    alias(libs.plugins.paparazziPlugin)
}

android {
    namespace = "com.sarim.screenshot_drawing_screen_type4"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
}

configurations.configureEach {
    if (name == "debugUnitTestRuntimeClasspath") {
        exclude(
            group = libs.archUnitJUnit4Library.get().group,
            module = libs.archUnitJUnit4Library.get().name,
        )
    }
}

dependencies {

    implementation(libs.androidxCoreKtxLibrary)
    implementation(libs.androidxLifecycleRuntimeKtxLibrary)
    implementation(libs.androidxActivityComposeLibrary)
    implementation(platform(libs.androidxComposeBomLibrary))
    implementation(libs.bundles.composeImplementationBundle)
    implementation(project(":example-app:example-app-presentation"))

    testImplementation(project(":nav"))
    testImplementation(platform(libs.androidxComposeBomLibrary))
    testImplementation(libs.composeJunit4Library)
    testImplementation(libs.bundles.testBundle)
}
