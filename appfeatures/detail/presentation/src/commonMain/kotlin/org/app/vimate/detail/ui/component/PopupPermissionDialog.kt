package org.app.vimate.detail.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.detail_allow_permission
import org.app.vimate.appres.resources.detail_popup_title
import org.app.vimate.appres.resources.ic_c_close
import org.app.vimate.appres.resources.ic_p_pause
import org.jetbrains.compose.resources.stringResource

/** VidMate's "Play videos in floating window" permission card. */
@Composable
internal fun PopupPermissionDialog(onAllow: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.width(264.dp).clip(RoundedCornerShape(20.dp)).background(ViColors.surface), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.fillMaxWidth().height(160.dp).background(Brush.verticalGradient(listOf(ViColors.detailPopupTop, ViColors.white)))) {
                Box(Modifier.align(Alignment.TopEnd).padding(12.dp).size(24.dp).clickable(onClick = onDismiss), contentAlignment = Alignment.Center) {
                    Ic(Res.drawable.ic_c_close, Modifier.size(20.dp), ViColors.detailLikeDivider)
                }
                Box(Modifier.align(Alignment.Center).size(140.dp, 110.dp).clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(ViColors.detailPopupGradStart, ViColors.detailPopupGradEnd))))
                Box(Modifier.align(Alignment.Center).offset(x = 34.dp, y = 6.dp).size(98.dp, 56.dp).clip(RoundedCornerShape(8.dp)).background(ViColors.detailPopupScreen), contentAlignment = Alignment.Center) {
                    Ic(Res.drawable.ic_p_pause, Modifier.size(20.dp), ViColors.white)
                }
            }
            Text(stringResource(Res.string.detail_popup_title), Modifier.padding(top = 18.dp), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ViColors.ink)
            Box(Modifier.padding(16.dp).fillMaxWidth().height(36.dp).clip(RoundedCornerShape(8.dp)).background(ViColors.detailPopupAllow).clickable(onClick = onAllow), contentAlignment = Alignment.Center) {
                Text(stringResource(Res.string.detail_allow_permission), color = ViColors.white, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
