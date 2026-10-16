package org.app.vimate.sharedlogic.di

import java.util.prefs.Preferences
import org.app.kmediaplayer.settings.KeyValueStore
import org.app.vimate.sharedlogic.data.StoreFactory
import org.app.vimate.sharedlogic.platform.DesktopPlatformActions
import org.app.vimate.sharedlogic.platform.DeviceLibrary
import org.app.vimate.sharedlogic.platform.NoDeviceLibrary
import org.app.vimate.sharedlogic.platform.PlatformActions
import org.koin.core.module.Module
import org.koin.dsl.module

private class PreferencesStore(private val node: Preferences) : KeyValueStore {
    override fun getString(key: String): String? = node.get(key, null)
    override fun putString(key: String, value: String?) { if (value == null) node.remove(key) else node.put(key, value) }
    override fun keys(): Set<String> = node.keys().toSet()
}

actual fun platformDataModule(): Module = module {
    single<StoreFactory> { StoreFactory { name -> PreferencesStore(Preferences.userRoot().node("org/app/vimate/$name")) } }
    single<PlatformActions> { DesktopPlatformActions() }
    single<DeviceLibrary> { NoDeviceLibrary() }
}
