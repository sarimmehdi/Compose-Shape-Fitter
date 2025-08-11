plugins {
    alias(libs.plugins.androidApplicationPlugin)
    alias(libs.plugins.kotlinAndroidPlugin)
    alias(libs.plugins.kotlinComposePlugin)
    alias(libs.plugins.kotlinSerializationPlugin)
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
        buildConfig = true
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
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
    implementation(project(":example-app:example-app-di"))
    implementation(project(":example-app:example-app-data"))
    implementation(project(":example-app:example-app-domain"))
    implementation(project(":example-app:example-app-presentation"))
    implementation(project(":utils"))
    implementation(kotlin("reflect"))

    debugImplementation(libs.bundles.composeDebugImplementationBundle)

    testImplementation(platform(libs.androidxComposeBomLibrary))
    testImplementation(libs.composeJunit4Library)
    testImplementation(libs.bundles.testBundle)

    androidTestImplementation(platform(libs.androidxComposeBomLibrary))
    androidTestImplementation(libs.composeJunit4Library)
    androidTestImplementation(libs.bundles.androidTestBundle)
    androidTestImplementation(project(":compose-shape-fitter"))
}
