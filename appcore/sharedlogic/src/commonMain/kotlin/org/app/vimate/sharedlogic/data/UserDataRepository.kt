package org.app.vimate.sharedlogic.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import org.app.kmediaplayer.settings.KeyValueStore
import org.app.vimate.sharedlogic.model.UserData

/** Profile, history, playlists, files, likes and the other user records, persisted on every change. */
class UserDataRepository(private val store: KeyValueStore) {
    private val _data = MutableStateFlow(UserData.decode(store.getString(KEY)))
    val data: StateFlow<UserData> = _data.asStateFlow()
    val value: UserData get() = _data.value

    fun update(transform: (UserData) -> UserData) {
        val next = _data.updateAndGet(transform)
        store.putString(KEY, next.encode())
    }

    private companion object { const val KEY = "user_data" }
}
