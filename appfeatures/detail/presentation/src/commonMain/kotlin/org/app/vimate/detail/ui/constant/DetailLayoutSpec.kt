package org.app.vimate.detail.ui.constant

/** Fixed geometry of the video page, measured from VidMate (3px = 1dp). */
object DetailLayoutSpec {
    const val PLAYER_RATIO = 16f / 9
    /** Related video cards under the comments card. */
    const val RELATED_RATIO = 1008f / 607
    val SPEEDS = listOf(0.25f, 0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f)
    const val HIDE_CONTROLS_MS = 3500L
    const val POLL_MS = 500L
}
