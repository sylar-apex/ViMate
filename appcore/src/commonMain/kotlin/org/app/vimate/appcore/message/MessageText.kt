package org.app.vimate.appcore.message

import androidx.compose.runtime.Composable
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.*
import org.app.vimate.sharedlogic.event.AppMessage
import org.app.vimate.sharedlogic.event.MessageKey
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** The strings.xml text of each toast. */
val MessageKey.res: StringResource
    get() = when (this) {
        MessageKey.ENTER_PROFILE_NAME -> Res.string.msg_enter_profile_name
        MessageKey.SIGNED_IN -> Res.string.msg_signed_in
        MessageKey.PLAYBACK_UNAVAILABLE -> Res.string.msg_playback_unavailable
        MessageKey.ADDED_TO_DOWNLOADS -> Res.string.msg_added_to_downloads
        MessageKey.ADDED_N_TO_DOWNLOADS -> Res.string.msg_added_n_to_downloads
        MessageKey.ALREADY_QUEUED -> Res.string.msg_already_queued
        MessageKey.WIFI_ONLY -> Res.string.msg_wifi_only
        MessageKey.NOT_A_URL -> Res.string.msg_not_a_url
        MessageKey.PAGE_NOT_MEDIA -> Res.string.msg_page_not_media
        MessageKey.WATCH_LATER_ADDED -> Res.string.msg_watch_later_added
        MessageKey.WATCH_LATER_REMOVED -> Res.string.msg_watch_later_removed
        MessageKey.HIDDEN_FROM_HOME -> Res.string.msg_hidden_from_home
        MessageKey.REPORT_SAVED -> Res.string.msg_report_saved
        MessageKey.FOLLOWING -> Res.string.msg_following
        MessageKey.UNFOLLOWED -> Res.string.msg_unfollowed
        MessageKey.SET_PRIVATE_PASSWORD_FIRST -> Res.string.msg_set_private_password_first
        MessageKey.NO_PRIVATE_PASSWORD -> Res.string.msg_no_private_password
        MessageKey.PRIVATE_UNLOCKED -> Res.string.msg_private_unlocked
        MessageKey.PASSWORD_MISMATCH -> Res.string.msg_password_mismatch
        MessageKey.PASSWORDS_DIFFER -> Res.string.msg_passwords_differ
        MessageKey.PASSWORD_TOO_SHORT -> Res.string.msg_password_too_short
        MessageKey.PASSWORD_SAVED -> Res.string.msg_password_saved
        MessageKey.PRIVATE_LOCK_REMOVED -> Res.string.msg_private_lock_removed
        MessageKey.FEEDBACK_SAVED -> Res.string.msg_feedback_saved
        MessageKey.CACHE_FREED -> Res.string.msg_cache_freed
        MessageKey.LIKE_US_THANKS -> Res.string.msg_like_us_thanks
        MessageKey.UPDATE_CURRENT -> Res.string.msg_update_current
        MessageKey.PRIVACY_NOTE -> Res.string.msg_privacy_note
        MessageKey.IMPORT_ANDROID_ONLY -> Res.string.msg_import_android_only
        MessageKey.IMPORTED_ONE -> Res.string.msg_imported_one
        MessageKey.IMPORTED_MANY -> Res.string.msg_imported_many
        MessageKey.NO_DOWNLOADS_RUNNING -> Res.string.msg_no_downloads_running
        MessageKey.PAUSED_N -> Res.string.msg_paused_n
        MessageKey.NOTHING_TO_RESUME -> Res.string.msg_nothing_to_resume
        MessageKey.RESUMED_N -> Res.string.msg_resumed_n
        MessageKey.DELETED_N -> Res.string.msg_deleted_n
        MessageKey.NO_SUBTITLES -> Res.string.msg_no_subtitles
        MessageKey.WIDGET_CONFIRM -> Res.string.msg_widget_confirm
        MessageKey.WIDGET_LONG_PRESS -> Res.string.msg_widget_long_press
        MessageKey.WIDGET_ANDROID_ONLY -> Res.string.msg_widget_android_only
        MessageKey.DOWNLOAD_FAILED -> Res.string.msg_download_failed
    }

@Composable
fun AppMessage.text(): String = stringResource(key.res, *args.toTypedArray())
