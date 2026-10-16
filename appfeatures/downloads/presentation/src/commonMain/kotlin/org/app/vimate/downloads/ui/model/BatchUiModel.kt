package org.app.vimate.downloads.ui.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import org.app.vimate.downloads.domain.model.BatchItem

/**
 * Batch screen choices: the quality [preset], per-title [overrides], titles left out of the download
 * ([unchecked]) and, in edit mode, titles [marked] for removal.
 */
@Stable
class BatchUiModel(
    preset: String,
    expanded: Boolean = false,
    editing: Boolean = false,
    unchecked: Set<String> = emptySet(),
    marked: Set<String> = emptySet(),
    overrides: Map<String, String> = emptyMap(),
) {
    val preset = mutableStateOf(preset)
    val expanded = mutableStateOf(expanded)
    val editing = mutableStateOf(editing)
    val unchecked = mutableStateOf(unchecked)
    val marked = mutableStateOf(marked)
    val overrides = mutableStateOf(overrides)

    fun qualityOf(item: BatchItem): String = overrides.value[item.media.id] ?: item.qualityFor(preset.value)

    fun isChecked(id: String): Boolean = if (editing.value) id in marked.value else id !in unchecked.value

    fun toggle(id: String) {
        if (editing.value) marked.value = marked.value.flip(id) else unchecked.value = unchecked.value.flip(id)
    }

    fun choosePreset(value: String) {
        preset.value = value
        overrides.value = emptyMap()
    }

    fun override(id: String, quality: String) { overrides.value = overrides.value + (id to quality) }

    fun stopEditing() {
        editing.value = false
        marked.value = emptySet()
    }

    /** title id → quality for every checked title. */
    fun choices(items: List<BatchItem>): Map<String, String> =
        items.filter { it.media.id !in unchecked.value }.associate { it.media.id to qualityOf(it) }

    private fun Set<String>.flip(id: String) = if (id in this) this - id else this + id

    companion object {
        private const val SEP = "\u0001"

        val Saver = listSaver<BatchUiModel, String>(
            save = {
                listOf(
                    it.preset.value, it.expanded.value.toString(), it.editing.value.toString(),
                    it.unchecked.value.joinToString(SEP), it.marked.value.joinToString(SEP),
                    it.overrides.value.entries.joinToString(SEP) { e -> e.key + SEP + e.value },
                )
            },
            restore = { s ->
                fun set(raw: String) = raw.split(SEP).filter { it.isNotEmpty() }.toSet()
                val pairs = s[5].split(SEP).filter { it.isNotEmpty() }.chunked(2).filter { it.size == 2 }.associate { it[0] to it[1] }
                BatchUiModel(s[0], s[1].toBoolean(), s[2].toBoolean(), set(s[3]), set(s[4]), pairs)
            },
        )
    }
}
