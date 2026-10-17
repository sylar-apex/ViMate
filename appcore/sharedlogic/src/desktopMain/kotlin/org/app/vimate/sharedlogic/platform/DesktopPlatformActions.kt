package org.app.vimate.sharedlogic.platform

import java.awt.FileDialog
import java.awt.Frame
import java.awt.Toolkit
import javax.swing.JFileChooser
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

    override fun notify(title: String, body: String) {
        runCatching {
            if (System.getProperty("os.name").startsWith("Mac")) {
                // Notification Center through AppleScript; quotes escaped so a video title cannot break the script.
                fun q(s: String) = "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
                ProcessBuilder("osascript", "-e", "display notification ${q(body)} with title ${q(title)}").start()
            } else if (java.awt.SystemTray.isSupported()) {
                val tray = java.awt.SystemTray.getSystemTray()
                val icon = java.awt.TrayIcon(java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_ARGB), title)
                tray.add(icon)
                icon.displayMessage(title, body, java.awt.TrayIcon.MessageType.INFO)
            }
        }
    }

    override fun pinWidget(widget: HomeWidget) = WidgetPinResult.UNSUPPORTED

    override fun requestImport() {}

    override fun clearCache(): Long {
        val cache = File(System.getProperty("java.io.tmpdir"), CACHE_DIR)
        val freed = cache.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        cache.deleteRecursively()
        return freed
    }

    override fun pickFolder(): String? {
        // Native macOS folder sheet; Swing chooser elsewhere.
        if (System.getProperty("os.name").startsWith("Mac")) {
            System.setProperty("apple.awt.fileDialogForDirectories", "true")
            try {
                val dialog = FileDialog(null as Frame?, "Choose download folder", FileDialog.LOAD)
                dialog.isVisible = true
                return dialog.directory?.let { dir -> dialog.file?.let { File(dir, it).path } }
            } finally {
                System.setProperty("apple.awt.fileDialogForDirectories", "false")
            }
        }
        val chooser = JFileChooser().apply { fileSelectionMode = JFileChooser.DIRECTORIES_ONLY; dialogTitle = "Choose download folder" }
        return if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) chooser.selectedFile.path else null
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
