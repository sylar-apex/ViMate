package org.app.vimate.search.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.search.constant.HISTORY_SHOWN
import org.app.vimate.search.constant.HOT_FIRE_COUNT
import org.app.vimate.search.constant.HOT_SEARCHES
import org.jetbrains.compose.resources.stringResource

/** History + Hot Searches: 17sp bold headers at x=16, 26dp white chips 6dp apart, fire on the top two hot terms. */
@Composable
internal fun SearchHome(history: List<String>, onPick: (String) -> Unit, onClear: () -> Unit) {
    LazyColumn(contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 13.dp, top = 16.dp)) {
        if (history.isNotEmpty()) {
            item(key = "history_header") {
                Row(Modifier.fillMaxWidth().height(22.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(Res.string.search_history), Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 15.4.sp, lineHeight = 22.sp, color = ViColors.ink)
                    Box(Modifier.size(20.dp).clickable(onClick = onClear)) {
                        Ic(Res.drawable.ic_trash_t, Modifier.size(20.dp), ViColors.trashGrey, stringResource(Res.string.search_clear_history))
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
            item(key = "history") { Chips(history.take(HISTORY_SHOWN), hotCount = 0, onPick = onPick); Spacer(Modifier.height(16.dp)) }
        }
        item(key = "hot_header") {
            Text(stringResource(Res.string.search_hot), fontWeight = FontWeight.Bold, fontSize = 15.4.sp, lineHeight = 22.sp, color = ViColors.ink)
            Spacer(Modifier.height(12.dp))
        }
        item(key = "hot") { Chips(HOT_SEARCHES, hotCount = HOT_FIRE_COUNT, onPick = onPick) }
    }
}

/** Wrapping white chips (a few short terms; FlowRow has no lazy form). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun Chips(items: List<String>, hotCount: Int, onPick: (String) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEachIndexed { i, t ->
            Row(Modifier.height(26.dp).clip(CircleShape).background(ViColors.surface).clickable { onPick(t) }.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                if (i < hotCount) Ic(Res.drawable.ic_fire, Modifier.padding(end = 4.dp).size(14.dp), ViColors.fire)
                Text(t, fontSize = 12.2.sp, lineHeight = 17.7.sp, color = ViColors.ink)
            }
        }
    }
}

/** POC FlowChips: Material suggestion chips that wrap. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowChips(items: List<String>, onPick: (String) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { s -> SuggestionChip(onClick = { onPick(s) }, label = { Text(s) }, shape = RoundedCornerShape(16.dp)) }
    }
}
