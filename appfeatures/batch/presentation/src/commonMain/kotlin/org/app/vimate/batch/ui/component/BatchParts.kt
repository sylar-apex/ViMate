package org.app.vimate.batch.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.batch_back
import org.app.vimate.appres.resources.ic_back
import org.app.vimate.appres.resources.ic_check_t
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun Check(on: Boolean) {
    Box(Modifier.fillMaxSize().clip(CircleShape).then(if (on) Modifier.background(ViColors.batchCheckOn) else Modifier.border(1.5.dp, ViColors.batchCheckOff, CircleShape)),
        contentAlignment = Alignment.Center) {
        if (on) Ic(Res.drawable.ic_check_t, Modifier.size(12.dp), ViColors.white)
    }
}

/** POC MeScreen PageHeader: 50dp white, back chevron at x=16 (40dp), title at x=64, 1dp rule. */
@Composable
internal fun PageHeader(title: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(50.dp).background(ViColors.surface), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.padding(start = 16.dp).size(40.dp).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
            Ic(Res.drawable.ic_back, Modifier.size(30.dp), ViColors.ink, stringResource(Res.string.batch_back))
        }
        Text(title, Modifier.padding(start = 8.dp), fontSize = 19.sp, lineHeight = 27.sp, fontWeight = FontWeight.Bold, color = ViColors.ink)
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(ViColors.rule))
}
