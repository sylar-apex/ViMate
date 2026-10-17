package org.app.vimate

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.sharedlogic.settings.AppKeys

/**
 * Settings › Quick Search: a silent, ongoing notification whose tap opens the app's Search page, so a link
 * or query is one pull-down away. Shown and removed as the setting changes.
 */
internal object QuickSearchNotification {
    private const val CHANNEL = "quick_search"
    private const val ID = 7301

    fun bind(context: Context, settings: ObservableSettings, scope: CoroutineScope) {
        scope.launch {
            settings.version.map { settings[AppKeys.quickSearch] }.distinctUntilChanged().collect { on ->
                if (on) show(context) else context.getSystemService(NotificationManager::class.java).cancel(ID)
            }
        }
    }

    private fun show(context: Context) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return
        val manager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= 26 && manager.getNotificationChannel(CHANNEL) == null) {
            manager.createNotificationChannel(NotificationChannel(CHANNEL, context.getString(R.string.quick_search_channel), NotificationManager.IMPORTANCE_LOW))
        }
        val open = PendingIntent.getActivity(
            context, ID,
            Intent(context, MainActivity::class.java).putExtra(MainActivity.EXTRA_PAGE, MainActivity.PAGE_SEARCH)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .setContentTitle(context.getString(R.string.quick_search_title))
            .setContentText(context.getString(R.string.quick_search_text))
            .setContentIntent(open)
            .setOngoing(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        manager.notify(ID, notification)
    }
}
