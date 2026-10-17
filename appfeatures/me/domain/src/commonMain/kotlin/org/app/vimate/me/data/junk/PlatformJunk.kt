package org.app.vimate.me.data.junk

import org.app.vimate.me.domain.repository.JunkFileSystem
import org.koin.core.scope.Scope

/** Android: the POC folders; other platforms: the app cache through PlatformActions. */
expect fun Scope.platformJunkFileSystem(): JunkFileSystem
