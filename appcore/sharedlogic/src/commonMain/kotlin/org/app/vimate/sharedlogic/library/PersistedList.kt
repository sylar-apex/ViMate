package org.app.vimate.sharedlogic.library

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.app.kmediaplayer.settings.KeyValueStore

val AppJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }

/** A list persisted as one JSON value in [store] under [key], exposed as a flow. Small data only (POC JsonList). */
class PersistedList<T>(
    private val store: KeyValueStore,
    private val key: String,
    serializer: KSerializer<T>,
    private val limit: Int = 500,
) {
    private val listSerializer = ListSerializer(serializer)
    private val state = MutableStateFlow(load())
    val items: StateFlow<List<T>> = state.asStateFlow()

    private fun load(): List<T> = runCatching {
        AppJson.decodeFromString(listSerializer, store.getString(key)!!)
    }.getOrDefault(emptyList())

    fun update(transform: (List<T>) -> List<T>) {
        val next = state.updateAndGet { transform(it).take(limit) }
        store.putString(key, AppJson.encodeToString(listSerializer, next))
    }

    fun clear() = update { emptyList() }
}
