pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Compose Shape Fitter Sample App"
include(":app")
include(":compose-shape-fitter")
include(":screenshot")
include(":screenshot:screenshot-canvas-component")
include(":screenshot:screenshot-drawer-component")
include(":example-app")
include(":example-app:example-app-data")
include(":example-app:example-app-domain")
include(":example-app:example-app-presentation")
include(":utils")
include(":example-app:example-app-di")
include(":screenshot:screenshot-topbar-component")
include(":screenshot:screenshot-drawing-screen")
include(":screenshot:screenshot-drawing-screen:screenshot-drawing-screen-type1")
include(":screenshot:screenshot-drawing-screen:screenshot-drawing-screen-type2")
include(":screenshot:screenshot-drawing-screen:screenshot-drawing-screen-type3")
include(":screenshot:screenshot-drawing-screen:screenshot-drawing-screen-type4")
include(":screenshot:screenshot-drawing-screen:screenshot-drawing-screen-type5")
