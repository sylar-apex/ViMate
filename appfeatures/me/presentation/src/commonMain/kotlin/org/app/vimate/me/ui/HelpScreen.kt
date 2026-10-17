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
import org.app.vimate.me.domain.constant.MeUrls
import org.app.vimate.me.ui.constant.guides
import org.app.vimate.me.ui.constant.hotQuestions

/** Help Center: browser-style header, red "How to download?" banner, 4 app tabs, STEP cards, red "Go to <app>" button. */
@Composable
fun HelpScreen() {
    val nav = LocalAppNavigator.current
    var tab by remember { mutableIntStateOf(0) }
    val g = guides[tab]
    Column(Modifier.fillMaxSize().background(ViColors.page).statusBarsPadding().background(ViColors.surface)) {
        Box(Modifier.fillMaxWidth().height(50.dp).background(ViColors.surface)) {
            Box(Modifier.padding(start = 16.dp).size(32.dp).align(Alignment.CenterStart).clickable { nav.back() }, contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_home_line, Modifier.size(30.dp), ViColors.ink) }
            Text(stringResource(Res.string.me_help_center), Modifier.align(Alignment.Center), fontWeight = FontWeight.Bold, fontSize = 19.sp, color = ViColors.ink)
            Box(Modifier.padding(start = 268.dp).size(32.dp).align(Alignment.CenterStart), contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_reload, Modifier.size(28.dp), ViColors.ink) }
            Box(Modifier.padding(start = 312.dp).size(32.dp).align(Alignment.CenterStart), contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_more_vert, Modifier.size(26.dp), ViColors.ink) }
        }
        LazyColumn(Modifier.weight(1f)) {
            item(key = "banner") {
                Box(Modifier.fillMaxWidth().height(145.dp).background(Brush.verticalGradient(listOf(ViColors.meHelpTop, ViColors.meRed))), contentAlignment = Alignment.Center) {
                    Text(stringResource(Res.string.me_how_to_download), color = ViColors.white, fontWeight = FontWeight.Bold, fontSize = 23.sp)
                }
                LazyRow(Modifier.fillMaxWidth().height(48.dp).background(ViColors.meHelpTabs), userScrollEnabled = false) {
                    itemsIndexed(guides, key = { _, x -> x.name }) { i, x ->
                        Box(Modifier.fillParentMaxWidth(1f / guides.size).fillMaxHeight().background(if (i == tab) ViColors.white else ViColors.transparent, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                            .clickable { tab = i }, contentAlignment = Alignment.Center) {
                            if (x.svg != null) Svg(x.svg, Modifier.size(24.dp)) else Ic(x.glyph!!, Modifier.size(26.dp), x.tint)
                        }
                    }
                }
                Text(stringResource(Res.string.me_how_to_use), Modifier.padding(start = 16.dp, top = 22.dp, bottom = 14.dp), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ViColors.ink)
            }
            items(g.steps.size, key = { "$tab-$it" }) { i ->
                Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Box(Modifier.background(ViColors.meButtonRed).padding(horizontal = 6.dp, vertical = 2.dp)) { Text(stringResource(Res.string.me_step, i + 1), color = ViColors.white, fontSize = 13.sp) }
                    Text(stringResource(g.steps[i]), Modifier.padding(top = 10.dp), fontSize = 15.sp, color = ViColors.ink)
                }
                if (i < g.steps.lastIndex) Box(Modifier.padding(vertical = 12.dp).fillMaxWidth().height(8.dp).background(ViColors.meStepGap))
            }
        }
        Row(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp, top = 6.dp).fillMaxWidth().height(43.dp).background(ViColors.meButtonRed).clickable { nav.go(Routes.Browser(g.url)) },
            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            if (g.svg != null) Svg(g.svg, Modifier.size(28.dp)) else Box(Modifier.size(28.dp).clip(RoundedCornerShape(6.dp)).background(ViColors.surface), contentAlignment = Alignment.Center) { Ic(g.glyph!!, Modifier.size(22.dp), g.tint) }
            Text(stringResource(Res.string.me_go_to, g.name), Modifier.padding(start = 12.dp), color = ViColors.white, fontSize = 17.sp)
        }
    }
}

/** Help&Feedback: grey "Hot Questions" band, numbered rows with chevrons, bottom "All Questions | Feedback" bar. */
@Composable
fun FeedbackScreen() {
    val nav = LocalAppNavigator.current
    val toast = LocalToast.current
    val uri = androidx.compose.ui.platform.LocalUriHandler.current
    var open by remember { mutableStateOf(-1) }
    var writing by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(ViColors.page).statusBarsPadding().background(ViColors.surface)) {
        PageHeader(stringResource(Res.string.me_help_feedback), bold = false) { nav.back() }
        LazyColumn(Modifier.weight(1f)) {
            item(key = "band") {
                Box(Modifier.fillMaxWidth().height(50.dp).background(ViColors.meStepGap).padding(start = 13.dp), contentAlignment = Alignment.CenterStart) {
                    Text(stringResource(Res.string.me_hot_questions), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ViColors.ink)
                }
            }
            items(hotQuestions.size, key = { it }) { i ->
                Column(Modifier.fillMaxWidth().clickable { open = if (open == i) -1 else i }) {
                    Row(Modifier.fillMaxWidth().heightIn(min = 49.dp).padding(start = 13.dp, end = 13.dp, top = 12.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(Res.string.me_numbered, i + 1, stringResource(hotQuestions[i].first)), Modifier.weight(1f), fontSize = 15.5.sp, lineHeight = 21.sp, color = ViColors.ink)
                        Ic(Res.drawable.ic_chev_right, Modifier.padding(start = 8.dp).size(16.dp), ViColors.meArrow)
                    }
                    if (open == i) Text(stringResource(hotQuestions[i].second), Modifier.padding(start = 13.dp, end = 13.dp, bottom = 10.dp), fontSize = 13.sp, color = ViColors.meAnswer)
                    Box(Modifier.padding(start = 56.dp).fillMaxWidth().height(0.7.dp).background(ViColors.meDivider))
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(0.7.dp).background(ViColors.meDivider))
        Row(Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f).fillMaxHeight().clickable { open = -1 }, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Ic(Res.drawable.ic_list, Modifier.size(20.dp), ViColors.meRed); Text(stringResource(Res.string.me_all_questions), fontSize = 15.sp, color = ViColors.ink)
            }
            Box(Modifier.size(0.7.dp, 24.dp).background(ViColors.meRowRule))
            Row(Modifier.weight(1f).fillMaxHeight().clickable { writing = true }, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Ic(Res.drawable.ic_edit, Modifier.size(20.dp), ViColors.meRed); Text(stringResource(Res.string.me_feedback_tab), fontSize = 15.sp, color = ViColors.ink)
            }
        }
    }
    if (writing) {
        var text by remember { mutableStateOf("") }
        val subject = stringResource(Res.string.me_feedback_subject)
        val thanks = stringResource(Res.string.me_feedback_thanks)
        AlertDialog(onDismissRequest = { writing = false }, title = { Text(stringResource(Res.string.me_feedback)) },
            text = { OutlinedTextField(text, { text = it }, Modifier.height(140.dp), placeholder = { Text(stringResource(Res.string.me_describe_problem)) }) },
            confirmButton = { TextButton(enabled = text.isNotBlank(), onClick = {
                runCatching { uri.openUri("mailto:?subject=" + encode(subject) + "&body=" + encode(text)) }
                writing = false; toast(thanks)
            }) { Text(stringResource(Res.string.me_submit)) } })
    }
}

/** Percent-encodes a mailto query value. */
private fun encode(s: String): String = buildString {
    s.encodeToByteArray().forEach { b ->
        val c = b.toInt() and 0xFF
        if (c.toChar().isLetterOrDigit() && c < 128 || c.toChar() in "-_.~") append(c.toChar()) else append('%').append(c.toString(16).uppercase().padStart(2, '0'))
    }
}

@Composable
fun AppsScreen() {
    val nav = LocalAppNavigator.current
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        BackBar(stringResource(Res.string.me_apps), onBack = { nav.back() })
        Text(stringResource(Res.string.me_apps_note), color = ViColors.muted, modifier = Modifier.padding(16.dp))
        Button(onClick = { nav.go(Routes.Browser(MeUrls.PLAY_STORE)) }, Modifier.padding(16.dp), colors = ButtonDefaults.buttonColors(containerColor = VRed)) { Text(stringResource(Res.string.me_open_play_store)) }
    }
}
