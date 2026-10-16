package org.app.vimate.sharedlogic.platform

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.io.File
import org.app.vimate.sharedlogic.model.StorageInfo

/** Desktop has no share sheet: sharing copies the text to the clipboard. */
class DesktopPlatformActions : PlatformActions {
    override val supportsImport = false

    override fun share(text: String, uri: String?) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(listOfNotNull(text, uri).joinToString("\n")), null)
    }

    override fun clipboardText(): String? = runCatching {
        Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor) as? String
    }.getOrNull()

    override fun notify(title: String, body: String) {}

    override fun pinWidget(widget: HomeWidget) = WidgetPinResult.UNSUPPORTED

    override fun requestImport() {}

    override fun clearCache(): Long {
        val cache = File(System.getProperty("java.io.tmpdir"), CACHE_DIR)
        val freed = cache.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        cache.deleteRecursively()
        return freed
    }

    override fun storageInfo(): StorageInfo {
        val root = File(System.getProperty("user.home"))
        return StorageInfo(root.usableSpace, root.totalSpace)
    }

    companion object {
        /** Folder under the system temp directory holding partial downloads. */
        const val CACHE_DIR = "vimate"
    }
}
