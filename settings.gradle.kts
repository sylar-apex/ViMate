rootProject.name = "ViMate"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":androidApp")
include(":desktopApp")
include(":shared")
include(":webApp")

// Core KMP libraries (no app-specific code)
include(":kmpcore:mvi")
include(":kmpcore:navigation")
include(":kmpcore:di")
include(":kmpcore:utils")
include(":kmpcore:permissions")
include(":kmpcore:storage:datastore")
include(":kmpcore:networking")

// Reusable media player library — core = engine, models, logic; ui = Compose player screens
include(":kmediaplayer:core")
include(":kmediaplayer:ui")

// Reusable download library — queue, resume, segmented downloads, per-platform storage
include(":kdownloader:core")

// App-level shared code
include(":appcore")
include(":appcore:appres")
include(":appcore:sharedlogic")

// One module per screen, each split into domain + presentation
val features = listOf("onboarding", "player", "home", "detail", "downloads")
features.forEach { feature ->
    include(":appfeatures:$feature:domain")
    include(":appfeatures:$feature:presentation")
}
