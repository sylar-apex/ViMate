package org.app.vimate.sharedlogic.di

import org.app.kmediaplayer.settings.KeyValueStore
import org.app.vimate.sharedlogic.data.StoreFactory
import org.app.vimate.sharedlogic.platform.DeviceLibrary
import org.app.vimate.sharedlogic.platform.NoDeviceLibrary
import org.app.vimate.sharedlogic.platform.PlatformActions
import org.app.vimate.sharedlogic.platform.WebPlatformActions
import org.koin.core.module.Module
import org.koin.dsl.module

@JsFun("(k) => window.localStorage.getItem(k)")
private external fun lsGet(key: String): String?

@JsFun("(k, v) => window.localStorage.setItem(k, v)")
private external fun lsSet(key: String, value: String)

@JsFun("(k) => window.localStorage.removeItem(k)")
private external fun lsRemove(key: String)

private class LocalStorageStore(private val prefix: String) : KeyValueStore {
    private val known = mutableSetOf<String>()
    override fun getString(key: String): String? = lsGet("$prefix.$key")
    override fun putString(key: String, value: String?) {
        if (value == null) { lsRemove("$prefix.$key"); known -= key } else { lsSet("$prefix.$key", value); known += key }
    }
    override fun keys(): Set<String> = known.toSet()
}

actual fun platformDataModule(): Module = module {
    single<StoreFactory> { StoreFactory { name -> LocalStorageStore("vimate.$name") } }
    single<PlatformActions> { WebPlatformActions() }
    single<DeviceLibrary> { NoDeviceLibrary() }
}
