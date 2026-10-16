package org.app.vimate.sharedlogic.data

import org.app.kmediaplayer.settings.KeyValueStore

/** Opens named key-value files on the current platform. */
fun interface StoreFactory {
    fun open(name: String): KeyValueStore
}
