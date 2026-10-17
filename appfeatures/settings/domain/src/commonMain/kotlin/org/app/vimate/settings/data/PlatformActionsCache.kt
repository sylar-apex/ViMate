package org.app.vimate.settings.data

import org.app.vimate.settings.domain.repository.AppCache
import org.app.vimate.sharedlogic.platform.PlatformActions

internal class PlatformActionsCache(private val platform: PlatformActions) : AppCache {
    override fun size() = 0L
    override fun clear() { platform.clearCache() }
}
