package org.app.vimate.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.RadioDot
import org.app.vimate.appcore.message.res
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.report_done
import org.app.vimate.appres.resources.report_title
import org.app.vimate.sharedlogic.model.ReportReason
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReportSheet(mediaId: String, viewModel: ReportViewModel = koinViewModel()) {
    ReportContent(onDone = { viewModel.setEvent(ReportContract.Event.Submit(mediaId, it)) })
}

/** Pick one reason, then Done. Done stays grey until a reason is picked. */
@Composable
fun ReportContent(onDone: (ReportReason) -> Unit = {}) {
    var reason by rememberSaveable { mutableStateOf<ReportReason?>(null) }
    val ink = MaterialTheme.colorScheme.onSurface
    Column(Modifier.fillMaxWidth().navigationBarsPadding()) {
        Text(
            stringResource(Res.string.report_title), color = ink, fontSize = 16.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 15.dp, bottom = 22.dp),
        )
        LazyColumn(userScrollEnabled = false) {
            items(ReportReason.entries, key = { it.name }) { item ->
                Row(Modifier.fillMaxWidth().height(44.dp).clickable { reason = item }.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(item.res), color = ink, fontSize = 15.sp, modifier = Modifier.weight(1f))
                    RadioDot(reason == item, size = 20.dp, dot = 11.dp, border = 1.dp, offColor = ViColors.reportRadioOff)
                }
            }
        }
        Spacer(Modifier.height(22.dp))
        val picked = reason
        Box(
            Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(40.dp).clip(CircleShape)
                .background(if (picked != null) ViColors.redButton else ViColors.disabledButton)
                .clickable(enabled = picked != null) { picked?.let(onDone) },
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(Res.string.report_done), color = ViColors.white, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(4.dp))
    }
}
