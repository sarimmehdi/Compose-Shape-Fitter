import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.androidLibraryPlugin)
    alias(libs.plugins.kotlinAndroidPlugin)
    alias(libs.plugins.kotlinComposePlugin)
    alias(libs.plugins.ktlintPlugin)
    alias(libs.plugins.detektPlugin)
    alias(libs.plugins.vanniktechMavenPublishingPlugin)
}

android {
    namespace = "com.sarim.compose_shape_fitter"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
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
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
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

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates("io.github.sarimmehdi", "compose-shape-fitter", "1.0.0")

    pom {
        name = "Compose Shape Fitter"
        description = "A library for fitting different shapes for a given set of points"
        inceptionYear = "2025"
        url = "https://github.com/sarimmehdi/Compose-Shape-Fitter"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "sarimmehdi"
                name = "Muhammad Sarim Mehdi"
                url = "https://github.com/sarimmehdi"
            }
        }
        scm {
            url = "https://github.com/sarimmehdi/Compose-Shape-Fitter"
            connection = "scm:git:git://github.com/sarimmehdi/Compose-Shape-Fitter.git"
            developerConnection = "scm:git:ssh://git@github.com/sarimmehdi/Compose-Shape-Fitter.git"
        }
    }
}

dependencies {

    implementation(libs.androidxCoreKtxLibrary)
    implementation(platform(libs.androidxComposeBomLibrary))
    implementation(libs.androidxMaterial3Library)
}
