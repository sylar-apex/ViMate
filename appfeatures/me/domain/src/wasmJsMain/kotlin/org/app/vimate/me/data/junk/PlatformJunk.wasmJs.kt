package org.app.vimate.me.data.junk

import org.app.vimate.me.domain.repository.JunkFileSystem
import org.koin.core.scope.Scope

actual fun Scope.platformJunkFileSystem(): JunkFileSystem = CacheOnlyJunkFileSystem(get())
