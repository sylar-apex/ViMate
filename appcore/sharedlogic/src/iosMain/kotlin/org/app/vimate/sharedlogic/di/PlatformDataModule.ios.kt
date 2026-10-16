package org.app.vimate.sharedlogic.di

import org.app.kmediaplayer.settings.KeyValueStore
import org.app.vimate.sharedlogic.data.StoreFactory
import org.app.vimate.sharedlogic.platform.DeviceLibrary
import org.app.vimate.sharedlogic.platform.IosPlatformActions
import org.app.vimate.sharedlogic.platform.NoDeviceLibrary
import org.app.vimate.sharedlogic.platform.PlatformActions
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

private class UserDefaultsStore(private val prefix: String) : KeyValueStore {
    private val defaults = NSUserDefaults.standardUserDefaults
    override fun getString(key: String): String? = defaults.stringForKey("$prefix.$key")
    override fun putString(key: String, value: String?) {
        if (value == null) defaults.removeObjectForKey("$prefix.$key") else defaults.setObject(value, "$prefix.$key")
    }
    override fun keys(): Set<String> = defaults.dictionaryRepresentation().keys.mapNotNull { (it as? String)?.removePrefix("$prefix.")?.takeIf { k -> k != it } }.toSet()
}

actual fun platformDataModule(): Module = module {
    single<StoreFactory> { StoreFactory { name -> UserDefaultsStore(name) } }
    single<PlatformActions> { IosPlatformActions() }
    single<DeviceLibrary> { NoDeviceLibrary() }
}
