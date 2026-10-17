package org.app.vimate.settings.domain.repository

/** POC Settings › Clear Cache: app cache size, and deleting it except partial downloads ("dl"). */
interface AppCache {
    fun size(): Long
    fun clear()
}
