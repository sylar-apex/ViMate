package org.app.vimate.sharedlogic.di

import java.util.prefs.Preferences
import org.app.kmediaplayer.settings.KeyValueStore
import org.app.vimate.sharedlogic.data.StoreFactory
import org.app.vimate.sharedlogic.platform.DesktopPlatformActions
import org.app.vimate.sharedlogic.platform.DeviceLibrary
import org.app.vimate.sharedlogic.platform.DesktopDeviceLibrary
import org.app.vimate.sharedlogic.platform.PlatformActions
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Desktop key-value store: one UTF-8 file per key under the app's data folder. java.util.prefs caps values at
 * 8 KB, which a long download queue exceeds; values already in Preferences are read once and moved to a file.
 */
private class FileStore(private val dir: java.io.File, private val legacy: Preferences) : KeyValueStore {
    init { dir.mkdirs() }
    private fun file(key: String) = java.io.File(dir, java.net.URLEncoder.encode(key, "UTF-8"))

    @Synchronized override fun getString(key: String): String? {
        val f = file(key)
        if (f.exists()) return f.readText()
        return legacy.get(key, null)?.also { putString(key, it); legacy.remove(key) }
    }

    @Synchronized override fun putString(key: String, value: String?) {
        val f = file(key)
        if (value == null) { f.delete(); return }
        val tmp = java.io.File(dir, f.name + ".tmp")
        tmp.writeText(value)
        if (!tmp.renameTo(f)) { f.delete(); tmp.renameTo(f) }
    }

    override fun keys(): Set<String> =
        (dir.listFiles()?.filter { !it.name.endsWith(".tmp") }?.map { java.net.URLDecoder.decode(it.name, "UTF-8") }.orEmpty() + legacy.keys()).toSet()
}

private val dataRoot = java.io.File(System.getProperty("user.home"), when {
    System.getProperty("os.name").startsWith("Mac") -> "Library/Application Support/ViMate"
    System.getProperty("os.name").startsWith("Windows") -> "AppData/Roaming/ViMate"
    else -> ".local/share/vimate"
})

actual fun platformDataModule(): Module = module {
    single<StoreFactory> { StoreFactory { name -> FileStore(java.io.File(dataRoot, name), Preferences.userRoot().node("org/app/vimate/$name")) } }
    single<PlatformActions> { DesktopPlatformActions() }
    single<DeviceLibrary> { DesktopDeviceLibrary { get<org.app.vimate.sharedlogic.download.vid.VidDownloads>().location() } }
}
