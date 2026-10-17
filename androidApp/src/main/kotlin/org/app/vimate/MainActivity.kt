package org.app.vimate

import org.app.vimate.appcore.navigation.Routes
import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import java.lang.ref.WeakReference
import org.app.vimate.sharedlogic.logic.LinkClassifier
import org.app.vimate.sharedlogic.model.ImportedMedia
import org.app.vimate.sharedlogic.platform.AndroidHost
import org.app.vimate.sharedlogic.platform.HostSignals
import org.app.vimate.sharedlogic.platform.mediaPermissions
import org.app.vimate.appcore.navigation.Tab
import org.app.vimate.shell.LaunchRequest
import org.app.vimate.shell.LaunchRequests
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val hostSignals: HostSignals by inject()

    private val mediaAccess = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { hostSignals.onResume() }
    private val importer = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        AndroidHost.onImported?.invoke(
            uris.map { uri ->
                runCatching { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
                val (name, size) = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)?.use { c ->
                    if (c.moveToFirst()) c.getString(0) to c.getLong(1) else null
                } ?: (uri.lastPathSegment.orEmpty() to 0L)
                ImportedMedia(name, uri.toString(), LinkClassifier.kindForUrl(name), size)
            },
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AndroidHost.activity = WeakReference(this)
        AndroidHost.importLauncher = { importer.launch(arrayOf("video/*", "audio/*")) }
        AndroidHost.mediaAccessLauncher = { mediaAccess.launch(mediaPermissions()) }
        // POC: notifications + media read on 33+, legacy storage below.
        if (Build.VERSION.SDK_INT >= 33) requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO), 1)
        else requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        handle(intent)
        setContent { App() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handle(intent)
    }

    override fun onResume() {
        super.onResume()
        AndroidHost.activity = WeakReference(this)
        hostSignals.onResume()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Copied Link Detection: Android only lets the focused app read the clipboard (HostWatchers reads it on resume).
        if (hasFocus) hostSignals.onResume()
    }

    private fun handle(intent: Intent?) {
        when {
            intent?.getStringExtra(EXTRA_TAB) == "files" -> LaunchRequests.post(LaunchRequest.OpenTab(Tab.FILES))
            intent?.getStringExtra(EXTRA_PAGE) == PAGE_SEARCH -> LaunchRequests.post(LaunchRequest.OpenPage(Routes.Search()))
            intent?.getStringExtra(EXTRA_VIDEO) != null -> LaunchRequests.post(LaunchRequest.OpenVideo(intent.getStringExtra(EXTRA_VIDEO)!!))
            intent?.action == Intent.ACTION_SEND -> LaunchRequests.postSharedText(intent.getStringExtra(Intent.EXTRA_TEXT))
            intent?.action == Intent.ACTION_VIEW -> intent.dataString?.let { LaunchRequests.post(LaunchRequest.OpenUrl(it)) }
        }
    }

    companion object {
        const val EXTRA_TAB = "tab"
        const val EXTRA_VIDEO = "video"
        /** Quick Search notification: open a page directly. */
        const val EXTRA_PAGE = "page"
        const val PAGE_SEARCH = "search"
    }
}
