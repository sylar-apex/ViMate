import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android {
        namespace = "org.app.vimate.sites.ui"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(libs.versions.javaVersion.get())
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "sitespresentation"
            isStatic = true
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.appcore)
            api(projects.appfeatures.sites.domain)
            implementation(libs.kotlin.serialization)
        }
        val nonAndroidMain by creating { dependsOn(commonMain.get()) }
        val desktopMain by getting {
            dependsOn(nonAndroidMain)
            dependencies {
                // Embedded browser so desktop opens a site in the app, same as the Home shortcuts.
                val fx = "21.0.6"
                val os = when {
                    System.getProperty("os.name").startsWith("Mac") -> "mac"
                    System.getProperty("os.name").startsWith("Windows") -> "win"
                    else -> "linux"
                }
                val arch = if (System.getProperty("os.arch") in setOf("aarch64", "arm64")) "aarch64" else "x64"
                val classifier = "$os-$arch"
                api("org.openjfx:javafx-base:$fx:$classifier")
                api("org.openjfx:javafx-graphics:$fx:$classifier")
                api("org.openjfx:javafx-controls:$fx:$classifier")
                api("org.openjfx:javafx-swing:$fx:$classifier")
                api("org.openjfx:javafx-media:$fx:$classifier")
                api("org.openjfx:javafx-web:$fx:$classifier")
            }
        }
        // iOS web view lives in src/iosMain, shared by both iOS targets.
        val iosMain = maybeCreate("iosMain").apply { dependsOn(nonAndroidMain) }
        iosArm64Main.get().dependsOn(iosMain)
        iosSimulatorArm64Main.get().dependsOn(iosMain)
        wasmJsMain.get().dependsOn(nonAndroidMain)
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
