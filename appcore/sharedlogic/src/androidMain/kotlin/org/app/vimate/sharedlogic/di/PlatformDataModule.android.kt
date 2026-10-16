package org.app.vimate.sharedlogic.di

import android.content.Context
import org.app.vimate.sharedlogic.data.PrefsStore
import org.app.vimate.sharedlogic.data.StoreFactory
import org.app.vimate.sharedlogic.platform.AndroidDeviceLibrary
import org.app.vimate.sharedlogic.platform.AndroidPlatformActions
import org.app.vimate.sharedlogic.platform.DeviceLibrary
import org.app.vimate.sharedlogic.platform.PlatformActions
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformDataModule(): Module = module {
    single<StoreFactory> { val context = get<Context>(); StoreFactory { name -> PrefsStore(context.getSharedPreferences(name, Context.MODE_PRIVATE)) } }
    single<PlatformActions> { AndroidPlatformActions(get()) }
    single<DeviceLibrary> { AndroidDeviceLibrary(get()) }
}
