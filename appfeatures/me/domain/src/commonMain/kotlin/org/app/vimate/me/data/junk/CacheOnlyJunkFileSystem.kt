package org.app.vimate.me.data.junk

import org.app.vimate.me.domain.model.JunkGroup
import org.app.vimate.me.domain.model.JunkKind
import org.app.vimate.me.domain.model.JunkPath
import org.app.vimate.me.domain.repository.JunkFileSystem
import org.app.vimate.sharedlogic.platform.PlatformActions

/**
 * Platforms without shared-storage folders: one "Cache junk" entry backed by [PlatformActions.clearCache].
 * Its size is only known once cleaned, so it is reported as found but empty.
 */
internal class CacheOnlyJunkFileSystem(private val platform: PlatformActions) : JunkFileSystem {
    override val rootPath = CACHE
    override fun groups() = listOf(JunkGroup(JunkKind.CACHE, listOf(JunkPath(CACHE, exists = true)), true))
    override fun exists(path: String) = path == CACHE
    override fun sizeOf(path: String, onEnter: (String) -> Unit): Long { onEnter(path); return 0 }
    override fun walkRoot(onEnter: (String) -> Unit) = onEnter(CACHE)
    override fun clear(path: String) { platform.clearCache() }
    override fun clearCacheKeepingDownloads() { platform.clearCache() }

    private companion object { const val CACHE = "cache" }
}
