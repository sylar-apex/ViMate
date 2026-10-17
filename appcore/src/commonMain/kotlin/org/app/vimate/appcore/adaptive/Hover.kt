package org.app.vimate.appcore.adaptive

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/** Pointer hover for desktop and web; always false on touch screens. */
class Hover(val source: MutableInteractionSource, private val state: State<Boolean>) {
    val hovered: Boolean get() = state.value
}

@Composable
fun rememberHover(): Hover {
    val source = remember { MutableInteractionSource() }
    return Hover(source, source.collectIsHoveredAsState())
}

fun Modifier.hover(h: Hover): Modifier = this.hoverable(h.source)
