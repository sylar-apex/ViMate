package org.app.vimate.sharedlogic.download.vid

import java.io.File

actual object MediaProbe {
    actual fun describe(uri: String, id: String): Pair<String?, Long>? = null

    actual fun saveText(folder: String, name: String, text: String) {
        val dir = File(System.getProperty("user.home"), "Downloads/$folder").apply { mkdirs() }
        File(dir, name).writeText(text)
    }
}
