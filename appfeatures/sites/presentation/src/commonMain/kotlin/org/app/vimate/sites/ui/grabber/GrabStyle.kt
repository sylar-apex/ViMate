package org.app.vimate.sites.ui.grabber

import androidx.compose.runtime.Composable
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.sharedlogic.yt.YouTube
import org.app.vimate.sites.logic.fmtSize
import org.app.vimate.sites.model.GrabType
import org.jetbrains.compose.resources.stringResource

/** Grab types shown as tabs. */
internal val grabTypes = GrabType.entries.filter { it != GrabType.Other }

@Composable
internal fun GrabType.label(): String = stringResource(
    when (this) {
        GrabType.Video -> Res.string.grab_type_video
        GrabType.Audio -> Res.string.grab_type_audio
        GrabType.Image -> Res.string.grab_type_image
        GrabType.Subtitle -> Res.string.grab_type_subtitle
        GrabType.Document -> Res.string.grab_type_document
        GrabType.Compressed -> Res.string.grab_type_compressed
        GrabType.Program -> Res.string.grab_type_program
        GrabType.Torrent -> Res.string.grab_type_torrent
        GrabType.Other -> Res.string.grab_type_other
    },
)

internal fun tagColor(t: GrabType) = when (t) {
    GrabType.Video -> ViColors.tagVideo
    GrabType.Audio -> ViColors.tagAudio
    GrabType.Image -> ViColors.tagImage
    GrabType.Subtitle -> ViColors.tagSubtitle
    GrabType.Document -> ViColors.tagDocument
    GrabType.Compressed -> ViColors.tagCompressed
    GrabType.Program -> ViColors.tagProgram
    GrabType.Torrent -> ViColors.tagTorrent
    GrabType.Other -> ViColors.tagOther
}

internal fun bigIcon(t: GrabType) = when (t) {
    GrabType.Image -> Res.drawable.ic_g_img
    GrabType.Video -> Res.drawable.ic_g_vid
    GrabType.Audio -> Res.drawable.ic_g_aud
    GrabType.Other -> Res.drawable.ic_g_link
    else -> typeIcon(t)
}

private fun typeIcon(t: GrabType) = when (t) {
    GrabType.Video -> Res.drawable.ic_meta_video
    GrabType.Audio -> Res.drawable.ic_meta_music
    GrabType.Subtitle -> Res.drawable.ic_g_sub
    GrabType.Document -> Res.drawable.ic_g_doc
    GrabType.Compressed -> Res.drawable.ic_g_zip
    GrabType.Program -> Res.drawable.ic_g_apk
    GrabType.Torrent -> Res.drawable.ic_g_torrent
    else -> Res.drawable.ic_g_file
}

internal fun meta(g: GrabItem) = listOfNotNull(
    g.resolution.ifEmpty { null },
    if (g.duration > 0) YouTube.formatDuration(g.duration / 1000) else null,
    when { g.size > 0 -> fmtSize(g.size); g.size == 0L -> null; else -> "…" },
    g.url.substringBefore('?').substringAfterLast('.', "").takeIf { it.length in 2..5 }?.uppercase(),
).joinToString("  ·  ")
