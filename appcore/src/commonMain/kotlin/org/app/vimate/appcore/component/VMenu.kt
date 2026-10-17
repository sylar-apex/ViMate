package org.app.vimate.appcore.component

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
        expanded, onDismiss, Modifier.width(width.dp).background(Color.White), offset = offset,
        shape = RoundedCornerShape(0.dp), containerColor = Color.White, shadowElevation = 6.dp, tonalElevation = 0.dp,
    ) {
        items.forEach { (icon, label, action) ->
            Row(Modifier.fillMaxWidth().height(48.dp).clickable { onDismiss(); action() }.padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Ic(icon, Modifier.size(20.dp), ViColors.menuIcon, label)
                Text(label, Modifier.padding(start = 16.dp), fontSize = 13.8.sp, color = ViColors.ink, maxLines = 1)
            }
        }
    }
}
