package org.app.vimate.appcore.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.theme.ViColors

/** POC SiteIcon (ui/HomeScreen.kt): 36dp coloured tile with a white icon, 11.5sp label. */
@Composable
fun SiteIcon(name: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier.width(72.dp), onClick: () -> Unit) {
    Column(modifier.fillMaxHeight().clickable(onClick = onClick).padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(36.dp).clip(RoundedCornerShape(9.dp)).background(color), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Text(name, fontSize = 11.5.sp, lineHeight = 16.sp, color = ViColors.muted, modifier = Modifier.padding(top = 7.dp), maxLines = 1)
    }
}
