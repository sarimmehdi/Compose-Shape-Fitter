plugins {
    `kotlin-dsl`
}

group = "com.sarim.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    compileOnly(libs.androidGradlePluginLibrary)
    compileOnly(libs.kotlinGradlePluginLibrary)
    implementation(libs.gordonPluginLibrary)
    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        create(libs.versions.conventionPluginJacocoName.get()) {
            id = libs.plugins.conventionPluginJacocoId.get().pluginId
            implementationClass = libs.versions.conventionPluginJacocoClass.get()
        }
        create(libs.versions.conventionPluginGordonName.get()) {
            id = libs.plugins.conventionPluginGordonId.get().pluginId
            implementationClass = libs.versions.conventionPluginGordonClass.get()
        }
    }
}