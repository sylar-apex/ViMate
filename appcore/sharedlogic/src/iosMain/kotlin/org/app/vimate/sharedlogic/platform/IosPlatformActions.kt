package org.app.vimate.sharedlogic.platform

import kotlinx.cinterop.ExperimentalForeignApi
import org.app.vimate.sharedlogic.model.StorageInfo
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileSystemFreeSize
import platform.Foundation.NSFileSystemSize
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSNumber
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard

class IosPlatformActions : PlatformActions {
    override val supportsImport = false

    override fun share(text: String, uri: String?) {
        val items = listOfNotNull<Any>(text, uri?.let { NSURL.URLWithString(it) })
        val controller = UIActivityViewController(items, null)
        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(controller, true, null)
    }

    override fun clipboardText(): String? = UIPasteboard.generalPasteboard.string

    override fun notify(title: String, body: String) {}

    override fun pinWidget(widget: HomeWidget) = WidgetPinResult.UNSUPPORTED

    override fun requestImport() {}

    @OptIn(ExperimentalForeignApi::class)
    override fun clearCache(): Long {
        val manager = NSFileManager.defaultManager
        val dir = NSTemporaryDirectory()
        var freed = 0L
        manager.contentsOfDirectoryAtPath(dir, null)?.forEach { name ->
            val path = dir + name
            val size = (manager.attributesOfItemAtPath(path, null)?.get("NSFileSize") as? NSNumber)?.longLongValue ?: 0L
            if (manager.removeItemAtPath(path, null)) freed += size
        }
        return freed
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun storageInfo(): StorageInfo {
        val attrs = NSFileManager.defaultManager.attributesOfFileSystemForPath(NSHomeDirectory(), null) ?: return StorageInfo.Unknown
        val free = (attrs[NSFileSystemFreeSize] as? NSNumber)?.longLongValue ?: 0L
        val total = (attrs[NSFileSystemSize] as? NSNumber)?.longLongValue ?: 0L
        return StorageInfo(free, total)
    }
}
