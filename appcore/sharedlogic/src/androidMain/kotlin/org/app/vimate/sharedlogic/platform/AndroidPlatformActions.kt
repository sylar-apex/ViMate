package org.app.vimate.sharedlogic.platform

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.app.vimate.sharedlogic.model.StorageInfo

class AndroidPlatformActions(private val context: Context) : PlatformActions {
    override val supportsImport = true

    override fun share(text: String, uri: String?) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            if (uri != null) {
                type = "*/*"
                putExtra(Intent.EXTRA_STREAM, Uri.parse(uri))
                clipData = ClipData.newRawUri("", Uri.parse(uri))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                type = "text/plain"
            }
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun clipboardText(): String? {
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = manager.primaryClip ?: return null
        if (clip.itemCount == 0) return null
        return clip.getItemAt(0).coerceToText(context)?.toString()
    }

    override fun notify(title: String, body: String) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= 26 && manager.getNotificationChannel(DOWNLOAD_CHANNEL) == null) {
            manager.createNotificationChannel(NotificationChannel(DOWNLOAD_CHANNEL, title, NotificationManager.IMPORTANCE_DEFAULT))
        }
        val notification = NotificationCompat.Builder(context, DOWNLOAD_CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .build()
        manager.notify((System.currentTimeMillis() % NOTIFICATION_ID_RANGE).toInt(), notification)
    }

    override fun pinWidget(widget: HomeWidget): WidgetPinResult =
        AndroidHost.widgetPinner?.invoke(widget) ?: WidgetPinResult.ADD_BY_LONG_PRESS

    override fun requestImport() {
        AndroidHost.importLauncher?.invoke()
    }

    override fun clearCache(): Long {
        var freed = 0L
        context.cacheDir.listFiles()?.forEach { file ->
            freed += file.walkTopDown().filter { it.isFile }.sumOf { it.length() }
            file.deleteRecursively()
        }
        return freed
    }

    override fun storageInfo(): StorageInfo {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        return StorageInfo(stat.availableBytes, stat.totalBytes)
    }

    override fun refreshWidgets() {
        AndroidHost.widgetRefresher?.invoke()
    }

    private companion object {
        const val DOWNLOAD_CHANNEL = "downloads"
        const val NOTIFICATION_ID_RANGE = 100_000
    }
}
