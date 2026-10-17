package org.app.vimate.settings.data

import org.app.vimate.settings.domain.repository.AppCache
import org.koin.core.scope.Scope

actual fun Scope.platformAppCache(): AppCache = PlatformActionsCache(get())
