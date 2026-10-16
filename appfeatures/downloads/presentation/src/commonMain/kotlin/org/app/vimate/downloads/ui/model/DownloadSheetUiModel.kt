package org.app.vimate.downloads.ui.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import org.app.vimate.downloads.domain.model.DownloadForm
import org.app.vimate.downloads.domain.model.DownloadOptions

/** The file name being edited and the picked quality; seeded once from the options. */
@Stable
class DownloadSheetUiModel(name: String = "", choice: String = "", seeded: Boolean = false) {
    val name = mutableStateOf(name)
    val choice = mutableStateOf(choice)
    private val seeded = mutableStateOf(seeded)

    /** Fills the defaults the first time options arrive; later updates keep what the user changed. */
    fun seed(options: DownloadOptions) {
        if (seeded.value) return
        name.value = options.defaultName
        choice.value = options.defaultChoice
        seeded.value = true
    }

    fun toForm(options: DownloadOptions) = DownloadForm(options.media?.id, options.url, choice.value, name.value)

    companion object {
        val Saver = listSaver<DownloadSheetUiModel, Any>(
            save = { listOf(it.name.value, it.choice.value, it.seeded.value) },
            restore = { DownloadSheetUiModel(it[0] as String, it[1] as String, it[2] as Boolean) },
        )
    }
}
