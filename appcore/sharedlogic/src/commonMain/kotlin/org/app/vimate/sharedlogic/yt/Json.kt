package org.app.vimate.sharedlogic.yt

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/** Small helpers for walking YouTube's deeply nested, schema-less JSON. */

operator fun JsonElement?.get(key: String): JsonElement? = (this as? JsonObject)?.get(key)
operator fun JsonElement?.get(index: Int): JsonElement? = (this as? JsonArray)?.getOrNull(index)

val JsonElement?.str: String? get() = (this as? JsonPrimitive)?.contentOrNull
val JsonElement?.arr: List<JsonElement> get() = (this as? JsonArray) ?: emptyList()

fun JsonElement?.path(vararg keys: Any): JsonElement? =
    keys.fold(this) { acc, k -> if (k is Int) acc[k] else acc[k as String] }

/** Text from either `{simpleText}` or `{runs:[{text}]}` or `{content}`. */
val JsonElement?.text: String?
    get() = this["simpleText"].str
        ?: this["runs"].arr.joinToString("") { it["text"].str.orEmpty() }.ifEmpty { null }
        ?: this["content"].str

/** Largest thumbnail url from a `{thumbnails:[...]}` or `{sources:[...]}` node. */
val JsonElement?.thumb: String?
    get() = (this["thumbnails"] ?: this["sources"]).arr.lastOrNull()?.get("url").str?.let {
        if (it.startsWith("//")) "https:$it" else it
    }

/** Depth-first search: every object stored under [key] anywhere in the tree. */
fun JsonElement?.findAll(key: String, out: MutableList<JsonElement> = mutableListOf()): List<JsonElement> {
    when (this) {
        is JsonObject -> for ((k, v) in this) {
            if (k == key) out += v else v.findAll(key, out)
        }
        is JsonArray -> forEach { it.findAll(key, out) }
        else -> {}
    }
    return out
}

fun JsonElement?.findFirst(key: String): JsonElement? {
    when (this) {
        is JsonObject -> {
            this[key]?.let { return it }
            for (v in values) v.findFirst(key)?.let { return it }
        }
        is JsonArray -> for (v in this) v.findFirst(key)?.let { return it }
        else -> {}
    }
    return null
}
