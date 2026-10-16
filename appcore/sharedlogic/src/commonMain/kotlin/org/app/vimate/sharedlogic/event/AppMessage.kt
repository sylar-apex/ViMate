package org.app.vimate.sharedlogic.event

/**
 * A short message for the app's toast. [key] names the text, which the UI layer resolves from
 * strings.xml; [args] fill its placeholders.
 */
data class AppMessage(val key: MessageKey, val args: List<String> = emptyList())

/** Every toast the app can show. The UI maps each key to a string resource. */
enum class MessageKey {
    ENTER_PROFILE_NAME,
    SIGNED_IN,
    PLAYBACK_UNAVAILABLE,
    ADDED_TO_DOWNLOADS,
    ADDED_N_TO_DOWNLOADS,
    ALREADY_QUEUED,
    WIFI_ONLY,
    NOT_A_URL,
    PAGE_NOT_MEDIA,
    WATCH_LATER_ADDED,
    WATCH_LATER_REMOVED,
    HIDDEN_FROM_HOME,
    REPORT_SAVED,
    FOLLOWING,
    UNFOLLOWED,
    SET_PRIVATE_PASSWORD_FIRST,
    NO_PRIVATE_PASSWORD,
    PRIVATE_UNLOCKED,
    PASSWORD_MISMATCH,
    PASSWORDS_DIFFER,
    PASSWORD_TOO_SHORT,
    PASSWORD_SAVED,
    PRIVATE_LOCK_REMOVED,
    FEEDBACK_SAVED,
    CACHE_FREED,
    LIKE_US_THANKS,
    UPDATE_CURRENT,
    PRIVACY_NOTE,
    IMPORT_ANDROID_ONLY,
    IMPORTED_ONE,
    IMPORTED_MANY,
    NO_DOWNLOADS_RUNNING,
    PAUSED_N,
    NOTHING_TO_RESUME,
    RESUMED_N,
    DELETED_N,
    NO_SUBTITLES,
    WIDGET_CONFIRM,
    WIDGET_LONG_PRESS,
    WIDGET_ANDROID_ONLY,
    DOWNLOAD_FAILED,
}
