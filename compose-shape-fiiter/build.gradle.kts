plugins {
    alias(libs.plugins.androidLibraryPlugin)
    alias(libs.plugins.kotlinAndroidPlugin)
    alias(libs.plugins.kotlinComposePlugin)
}

android {
    namespace = "com.sarim.compose_shape_fiiter"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    debugImplementation(libs.androidxUiTestManifestLibrary)
}
