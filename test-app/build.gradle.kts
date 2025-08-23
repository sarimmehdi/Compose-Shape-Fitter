plugins {
    alias(libs.plugins.androidApplicationPlugin)
    alias(libs.plugins.kotlinAndroidPlugin)
    alias(libs.plugins.kotlinComposePlugin)
    alias(libs.plugins.conventionPluginGordonId)
}

android {
    namespace = "com.sarim.test_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sarim.test_app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += listOf("test")
    productFlavors {
        create("normal") {
            dimension = "test"
            testInstrumentationRunner = "com.sarim.test_app.InstrumentationTestNormalRunner"
        }
        create("error") {
            dimension = "test"
            testInstrumentationRunner = "com.sarim.test_app.InstrumentationTestErrorRunner"
        }
        all {
            testOptions.animationsDisabled = true
            testOptions.execution = "ANDROIDX_TEST_ORCHESTRATOR"
        }
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
    packaging {
        resources {
            pickFirsts.add("META-INF/AL2.0")
            pickFirsts.add("META-INF/LGPL2.1")
            pickFirsts.add("META-INF/LICENSE.md")
            pickFirsts.add("META-INF/LICENSE-notice.md")
        }
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
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
    implementation(libs.bundles.composeImplementationBundle)
    implementation(project(":nav"))
    implementation(project(":utils"))

    androidTestImplementation(platform(libs.koinBomLibrary))
    androidTestImplementation(libs.bundles.koinBundle)
    androidTestImplementation(platform(libs.androidxComposeBomLibrary))
    androidTestImplementation(libs.composeJunit4Library)
    androidTestImplementation(libs.bundles.androidTestBundle)
    androidTestImplementation(libs.bundles.dataStorageBundle)
    androidTestImplementation(project(":example-app:example-app-di"))
    androidTestImplementation(project(":example-app:example-app-data"))
    androidTestImplementation(project(":example-app:example-app-domain"))
    androidTestImplementation(project(":example-app:example-app-presentation"))
    androidTestImplementation(project(":compose-shape-fitter"))

    androidTestUtil(libs.androidXTestOrchestratorLibrary)
}
