package org.app.vimate.appcore.component

import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.theme.ViColors
import org.jetbrains.compose.resources.DrawableResource

/** VidMate popup menu: square white card with shadow, 48dp rows, 24dp outline icon at 16dp, 16sp label at 56dp. */
@Composable
fun VMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    width: Int,
    items: List<Triple<DrawableResource, String, () -> Unit>>,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
) {
    DropdownMenu(
        expanded, onDismiss, Modifier.widthIn(min = width.dp).padding(vertical = 2.dp), offset = offset,
        shape = RoundedCornerShape(12.dp), containerColor = ViColors.surface, shadowElevation = 8.dp, tonalElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, ViColors.wideHairline),
    ) {
        items.forEach { (icon, label, action) ->
            Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp).height(38.dp).clip(RoundedCornerShape(8.dp)).clickable { onDismiss(); action() }.padding(start = 10.dp, end = 14.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Ic(icon, Modifier.size(18.dp), ViColors.sectionLabel, label)
                Text(label, Modifier.padding(start = 12.dp), fontSize = 13.5.sp, color = ViColors.ink, maxLines = 1)
            }
        }
    }
}
