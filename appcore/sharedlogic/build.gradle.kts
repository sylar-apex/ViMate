import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android {
        namespace = "org.app.vimate.sharedlogic"
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
            baseName = "sharedlogic"
            isStatic = true
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            api(projects.kmediaplayer.core)
            api(projects.kmpcore.di)
            api(projects.kmpcore.utils)
            api(projects.kmpcore.mvi)
            api(projects.kmpcore.networking)
            api(projects.kdownloader.core)
            api(libs.koin.core.viewmodel)
            implementation(libs.kotlin.serialization)
        }
        // NewPipeExtractor stream resolver, shared by Android and desktop (JVM).
        val jvmShared by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.newpipe.extractor)
                implementation(libs.okhttp)
            }
        }
        androidMain.get().dependsOn(jvmShared)
        val desktopMain by getting
        desktopMain.dependsOn(jvmShared)
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.ktx)
        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
