package org.app.vimate.sharedlogic.platform

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.app.kmediaplayer.settings.ObservableSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.app.vimate.sharedlogic.settings.AppKeys

/**
 * Copied Link Detection (POC MainActivity.onWindowFocusChanged): when the app comes back and the setting
 * is on, the first http(s) url in the clipboard is published once per distinct link in [copiedLink];
 * the shell shows its download prompt and calls [consume] when dismissed.
 */
class HostWatchers(
    private val signals: HostSignals,
    private val platform: PlatformActions,
    private val settings: ObservableSettings,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var lastClip: String? = null
    private val _copiedLink = MutableStateFlow<String?>(null)
    val copiedLink: StateFlow<String?> = _copiedLink.asStateFlow()

    fun start() {
        scope.launch { signals.resumed.collect { checkClipboard() } }
    }

    fun consume() { _copiedLink.value = null }

    private fun checkClipboard() {
        if (!settings[AppKeys.clipboardDetect]) return
        val text = platform.clipboardText()
        val url = text?.let { Regex("""https?://\S+""").find(it)?.value }
        if (url != null && url != lastClip) { lastClip = url; _copiedLink.value = url }
    }
}
