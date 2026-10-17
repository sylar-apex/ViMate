import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "org.app.vimate.MainKt"
        // `-PvlcPath=<dir with libvlc>` runs against a specific VLC build instead of the installed one.
        providers.gradleProperty("vlcPath").orNull?.let { jvmArgs += "-Dvlc.path=$it" }
        providers.gradleProperty("vlcVerbose").orNull?.let { jvmArgs += "-Dvlc.verbose=true" }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.app.vimate"
            packageVersion = "1.0.0"
            // JavaFX (in-app browser) needs the Swing interop module in the bundled runtime.
            modules("jdk.unsupported", "jdk.unsupported.desktop", "jdk.jsobject", "java.net.http")
        }
    }
}