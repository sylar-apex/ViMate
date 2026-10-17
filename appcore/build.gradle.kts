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
        namespace = "org.app.vimate.appcore"
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
            baseName = "appcore"
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
            api(projects.appcore.appres)
            api(projects.appcore.sharedlogic)
            api(projects.kmediaplayer.ui)
            api(projects.kmpcore.mvi)
            api(projects.kmpcore.navigation)
            api(projects.kmpcore.utils)
            api(libs.compose.material3)
            api(libs.compose.materialIconsExtended)
            api(libs.compose.uiBackhandler)
            api(libs.compose.uiToolingPreview)
            api(libs.androidx.lifecycle.runtimeCompose)
            api(libs.androidx.lifecycle.viewmodelCompose)
            api(libs.koin.compose.viewmodel)
            api(libs.coil.compose)
            implementation(libs.kotlin.serialization)
            implementation(libs.coil.network.ktor3)
            implementation(libs.coil.svg)
        }
        androidMain.dependencies {
            implementation(libs.coil.video)
            implementation(libs.ktor.client.android.engine)
        }
        val desktopMain by getting
        desktopMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinx.browser)
            implementation(libs.ktor.client.js)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
