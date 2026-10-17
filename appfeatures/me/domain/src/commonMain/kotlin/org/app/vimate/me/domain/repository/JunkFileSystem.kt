package org.app.vimate.me.domain.repository

import org.app.vimate.me.domain.model.JunkGroup

/** The folders POC JunkCleanScreen scans and cleans; Android walks real folders, other platforms use the app cache only. */
interface JunkFileSystem {
    /** Root shown in "Scanning:" before the walk starts. */
    val rootPath: String
    fun groups(): List<JunkGroup>
    fun exists(path: String): Boolean
    /** Total file bytes under [path]; [onEnter] gets each directory walked. */
    fun sizeOf(path: String, onEnter: (String) -> Unit): Long
    /** Walks shared storage for the progress line (POC maxDepth 4, 400 entries). */
    fun walkRoot(onEnter: (String) -> Unit)
    /** Deletes the contents of [path]. */
    fun clear(path: String)
    /** POC BoostScreen: deletes the app cache except partial downloads ("dl"). */
    fun clearCacheKeepingDownloads()
}
