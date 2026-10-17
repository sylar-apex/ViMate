package org.app.vimate.appcore.component

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

/** Click without ripple (POC MainActivity `clickableNoRipple`). */
fun Modifier.clickableNoRipple(onClick: () -> Unit) =
    this.clickable(interactionSource = null, indication = null, onClick = onClick)
