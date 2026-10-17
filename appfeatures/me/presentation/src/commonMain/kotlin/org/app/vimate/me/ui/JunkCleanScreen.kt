package org.app.vimate.me.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.vimate.appcore.component.*
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.me.ui.component.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.app.vimate.me.domain.util.fmtSize

/** Junk files, as VidMate (dp): orange gradient header 140 tall with 60sp total; salmon "Scanning:" strip 26;
 *  42dp group rows (18dp tinted icon, 15sp label, chevron, grey size, red check); folder sub-rows; red 40dp Clean button. */
@Composable
fun JunkCleanScreen(viewModel: JunkViewModel = koinViewModel()) {
    val nav = LocalAppNavigator.current
    val s by viewModel.uiState.collectAsStateWithLifecycle()
    val scanning = s.scanning
    val cleaned = s.cleaned
    val selected = s.selected
    val (num, unit) = fmtSize(if (cleaned) 0 else s.total).split(' ').let { it[0] to it.getOrElse(1) { "" } }
    Column(Modifier.fillMaxSize().background(ViColors.surface).statusBarsPadding()) {
        Column(Modifier.fillMaxWidth().height(172.dp).background(Brush.verticalGradient(listOf(ViColors.meJunkTop, ViColors.meJunkBottom)))) {
            Row(Modifier.height(54.dp).padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.padding(start = 10.dp).size(40.dp).clickable { nav.back() }, contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_back, Modifier.size(30.dp), ViColors.white, stringResource(Res.string.me_back)) }
                Text(stringResource(Res.string.me_junk_files), Modifier.padding(start = 24.dp), color = ViColors.white, fontSize = 19.sp)
            }
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom) {
                Text(num.substringBefore('.'), color = ViColors.white, fontSize = 54.sp, lineHeight = 60.sp, fontWeight = FontWeight.Bold)
                Text(" " + unit, Modifier.padding(bottom = 10.dp), color = ViColors.white, fontSize = 13.sp)
            }
            Text(stringResource(if (cleaned) Res.string.me_phone_clean else Res.string.me_junk_found), Modifier.fillMaxWidth(), color = ViColors.white, fontSize = 13.5.sp, textAlign = TextAlign.Center)
        }
        Box(Modifier.fillMaxWidth().height(28.dp).background(ViColors.meJunkStrip).padding(horizontal = 12.dp), contentAlignment = Alignment.CenterStart) {
            Text(if (scanning) stringResource(Res.string.me_scanning, s.current) else stringResource(Res.string.me_scan_complete), color = ViColors.white, fontSize = 12.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        LazyColumn(Modifier.weight(1f)) {
            s.groups.forEachIndexed { gi, g ->
                item(key = "g" + g.kind.name) {
                    Row(Modifier.fillMaxWidth().height(50.dp).clickable { viewModel.setEvent(JunkContract.Event.ToggleOpen(gi)) }.padding(start = 13.dp, end = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(18.dp).clip(RoundedCornerShape(5.dp)).background(g.kind.tint), contentAlignment = Alignment.Center) { Ic(g.kind.icon, Modifier.size(12.dp), ViColors.white) }
                        Text(stringResource(g.kind.label), Modifier.padding(start = 7.dp), fontSize = 14.sp, color = ViColors.ink)
                        Ic(if (g.open) Res.drawable.ic_chev_up else Res.drawable.ic_chev_down, Modifier.padding(start = 6.dp).size(14.dp), ViColors.meChevron)
                        Spacer(Modifier.weight(1f))
                        JunkSize(if (scanning && g.bytes == 0L) "…" else fmtSize(if (cleaned) 0 else g.bytes))
                        JunkCheck(g.checked && !cleaned) { viewModel.setEvent(JunkContract.Event.ToggleGroup(gi)) }
                    }
                }
                if (g.open) {
                    val shown = g.paths.withIndex().filter { (_, p) -> p.bytes > 0 || p.exists }
                    items(shown, key = { (_, p) -> g.kind.name + p.path }) { (pi, p) ->
                        Row(Modifier.fillMaxWidth().heightIn(min = 50.dp).padding(start = 13.dp, end = 13.dp, top = 6.dp, bottom = 6.dp), verticalAlignment = Alignment.Top) {
                            Ic(Res.drawable.ic_j_folder, Modifier.padding(top = 3.dp).size(18.dp), ViColors.meFolder)
                            Text(p.path.replace("/", "/​"), Modifier.padding(start = 7.dp).width(212.dp), fontSize = 13.sp, lineHeight = 16.sp, color = ViColors.ink)
                            Spacer(Modifier.weight(1f))
                            JunkSize(fmtSize(if (cleaned) 0 else p.bytes))
                            JunkCheck(p.checked && !cleaned) { viewModel.setEvent(JunkContract.Event.TogglePath(gi, pi)) }
                        }
                    }
                }
            }
        }
        val sel = fmtSize(selected)
        Box(Modifier.padding(start = 13.dp, end = 13.dp, bottom = 20.dp, top = 6.dp).fillMaxWidth().height(40.dp).clip(RoundedCornerShape(6.dp))
            .background(ViColors.meButtonRed).clickable(enabled = !scanning && !cleaned && selected > 0) { viewModel.setEvent(JunkContract.Event.Clean) },
            contentAlignment = Alignment.Center) {
            Text(if (cleaned) stringResource(Res.string.me_done) else stringResource(Res.string.me_clean_junk, sel.replace(" ", "").substringBefore('.') + sel.substringAfterLast(' ')),
                color = ViColors.white, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
        }
    }
}
