package org.app.vimate.settings.data

import org.app.vimate.settings.domain.repository.AppCache
import org.koin.core.scope.Scope

/** Android: Context.cacheDir; other platforms: PlatformActions.clearCache with an unknown size. */
expect fun Scope.platformAppCache(): AppCache
