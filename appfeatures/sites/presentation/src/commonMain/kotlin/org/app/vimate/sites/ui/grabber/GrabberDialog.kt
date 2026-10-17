package org.app.vimate.sites.ui.grabber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.app.vimate.sites.logic.ioDispatcher
import org.app.vimate.appcore.component.Empty
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.sites.logic.dedupeGrab
import org.app.vimate.sites.logic.fmtSize
import org.app.vimate.sites.model.GrabType
import org.app.vimate.sites.repository.GrabProbeRepository
import org.app.vimate.sites.usecase.EnqueueDirectUseCase
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/** Grabber: VidMate-theme screen listing everything on the current page by type, with preview, multi-select and bulk download. */
@Composable
fun GrabberDialog(pageUrl: String, pageTitle: String, urls: List<String>, userAgent: String?, onClose: () -> Unit) {
    val toast = LocalToast.current
    val probe = koinInject<GrabProbeRepository>()
    val enqueue = koinInject<EnqueueDirectUseCase>()
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val all = remember(urls) { dedupeGrab(urls).map(GrabItem::of) }
    // HLS renditions listed by a master playlist (video-only / audio-only parts) are folded into that master.
    var hlsChildren by remember { mutableStateOf(emptySet<String>()) }
    val items = remember(all, hlsChildren) { all.filter { it.url.substringBefore('?') !in hlsChildren } }
    val counts = remember(items) { items.groupingBy { it.type }.eachCount() }
    var filter by remember { mutableStateOf<GrabType?>(null) }
    val selected = remember { mutableStateListOf<String>() }
    var preview by remember { mutableStateOf<GrabItem?>(null) }
    val shown = items.filter { filter == null || it.type == filter }
        .sortedWith(compareBy<GrabItem> { it.type.ordinal }.thenByDescending { it.width.toLong() * it.height + it.size.coerceAtLeast(0) / 1000 })
    var gridPref by remember { mutableStateOf(true) }
    val gridMode = gridPref

    // Size + resolution, a few at a time.
    LaunchedEffect(all) {
        val hls = all.filter { ".m3u8" in it.url }
        if (hls.isNotEmpty()) hlsChildren = withContext(ioDispatcher) {
            hls.map { g -> async { probe.probeHls(GrabCand(g), userAgent, pageUrl).let { (p, children) -> g.apply(p); children } } }.awaitAll().flatten().toSet()
        }
    }
    LaunchedEffect(items) {
        // Media first (thumbnails/resolution), 6 at a time; plain links get a quick size check afterwards on their own lane.
        val media = items.filter { it.type == GrabType.Image || it.type == GrabType.Video || it.type == GrabType.Audio }
        val rest = items - media.toSet()
        val gate = Semaphore(6)
        media.filter { ".m3u8" !in it.url }.forEach { g -> launch(ioDispatcher) { gate.withPermit { g.apply(probe.probeMedia(GrabCand(g), userAgent, pageUrl)); if (g.size < 0) g.size = probe.headSize(g.url, pageUrl, userAgent) } } }
        val slow = Semaphore(3)
        rest.forEach { g -> launch(ioDispatcher) { slow.withPermit { if (!g.url.startsWith("magnet:")) g.size = probe.headSize(g.url, pageUrl, userAgent) } } }
    }

    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(Modifier.fillMaxSize().background(ViColors.pocLightBackground)) {
            Row(Modifier.fillMaxWidth().background(ViColors.surface).statusBarsPadding().height(48.dp).padding(start = 4.dp, end = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clickable(onClick = onClose), contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_back, Modifier.size(28.dp), ViColors.ink, stringResource(Res.string.sites_back)) }
                Column(Modifier.weight(1f).padding(start = 6.dp)) {
                    Text(stringResource(Res.string.grab_title), fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 18.sp, color = ViColors.ink)
                    Text(stringResource(Res.string.grab_subtitle, pageUrl.substringAfter("://").substringBefore('/'), items.size), fontSize = 11.sp, lineHeight = 13.sp, color = ViColors.metaGrey, maxLines = 1)
                }
                // Grid / list toggle.
                Box(Modifier.size(36.dp).clip(CircleShape).clickable { gridPref = !gridPref }, contentAlignment = Alignment.Center) {
                    Ic(if (gridPref) Res.drawable.ic_v_list else Res.drawable.ic_v_grid, Modifier.size(22.dp), ViColors.ink, stringResource(if (gridPref) Res.string.grab_list_view else Res.string.grab_grid_view))
                }
            }
            // Single-select type tabs (only types that exist, plus All).
            LazyRow(Modifier.fillMaxWidth().background(ViColors.surface), contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 2.dp, bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item(key = "all") { TypeChip(stringResource(Res.string.grab_all), items.size, filter == null) { filter = null } }
                items(grabTypes.filter { (counts[it] ?: 0) > 0 }, key = { it.name }) { t -> TypeChip(t.label(), counts[t] ?: 0, filter == t) { filter = t } }
            }
            val selItems = shown.filter { it.url in selected }
            val allOn = shown.isNotEmpty() && selItems.size == shown.size
            Row(Modifier.fillMaxWidth().height(34.dp).padding(start = 12.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.grab_shown, shown.size, filter?.label()?.lowercase() ?: stringResource(Res.string.grab_files)), Modifier.weight(1f), fontSize = 12.sp, color = ViColors.muted)
                Row(Modifier.clip(CircleShape).clickable {
                    if (allOn) selected.removeAll(shown.map { it.url }) else { selected.removeAll(shown.map { it.url }); selected.addAll(shown.map { it.url }) }
                }.padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(if (allOn) Res.string.grab_deselect_all else Res.string.grab_select_all), fontSize = 12.sp, color = VRed, fontWeight = FontWeight.Medium)
                }
            }
            Box(Modifier.weight(1f)) {
                if (shown.isEmpty()) Empty(stringResource(Res.string.grab_nothing))
                else if (gridMode) LazyVerticalGrid(
                    GridCells.Fixed(3), contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(shown.size, key = { shown[it].url }) { i ->
                        val g = shown[i]; val on = g.url in selected
                        GridTile(g, on, onToggle = { if (on) selected.remove(g.url) else selected.add(g.url) }, onPreview = { preview = g })
                    }
                } else LazyColumn(contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(shown, key = { it.url }) { g ->
                        val on = g.url in selected
                        GrabRow(g, on, onToggle = { if (on) selected.remove(g.url) else selected.add(g.url) }, onPreview = { preview = g })
                    }
                }
            }
            // Bottom bar: count + size, red Download.
            Row(Modifier.fillMaxWidth().background(ViColors.surface).navigationBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(Res.string.grab_selected, selItems.size), fontSize = 14.sp, color = ViColors.ink, fontWeight = FontWeight.Bold)
                    Text(fmtSize(selItems.sumOf { it.size.coerceAtLeast(0) }), fontSize = 12.sp, color = ViColors.metaGrey)
                }
                Row(Modifier.height(40.dp).clip(CircleShape).background(if (selItems.isEmpty()) ViColors.grabButtonDisabled else ViColors.grabButton)
                    .clickable(enabled = selItems.isNotEmpty()) {
                        selItems.forEach { enqueue(it.url, it.name.substringBeforeLast('.'), pageUrl, userAgent, it.type) }
                        val n = selItems.size
                        scope.launch { toast(getString(Res.string.grab_added, n)) }
                        onClose()
                    }.padding(horizontal = 22.dp), verticalAlignment = Alignment.CenterVertically) {
                    Ic(Res.drawable.ic_d_download, Modifier.size(16.dp), ViColors.white)
                    Text(stringResource(Res.string.grab_download), color = ViColors.white, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
    preview?.let { g -> PreviewDialog(g, userAgent, pageUrl) { preview = null } }
}

private fun GrabCand(g: GrabItem) = org.app.vimate.sites.model.GrabCandidate(g.url, g.type, g.variants)

@Composable
private fun GrabRow(g: GrabItem, on: Boolean, onToggle: () -> Unit, onPreview: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(ViColors.surface)
        .then(if (on) Modifier.border(1.5.dp, VRed, RoundedCornerShape(12.dp)) else Modifier)
        .clickable(onClick = onPreview).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(52.dp).clip(RoundedCornerShape(8.dp)).background(ViColors.grabListThumb), contentAlignment = Alignment.Center) {
            val t = g.thumb
            if (t != null) AsyncImage(t, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            else Ic(bigIcon(g.type), Modifier.size(24.dp), tagColor(g.type))
            Box(Modifier.align(Alignment.BottomStart).clip(RoundedCornerShape(topEnd = 4.dp)).background(tagColor(g.type)).padding(horizontal = 3.dp)) {
                Text((g.ext ?: g.type.label()).uppercase(), color = ViColors.white, fontSize = 8.sp, lineHeight = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(g.name, fontSize = 14.sp, color = ViColors.ink, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(meta(g), fontSize = 12.sp, color = ViColors.metaGrey, maxLines = 1)
        }
        Box(Modifier.size(40.dp).clickable(onClick = onToggle), contentAlignment = Alignment.Center) { Check(on) }
    }
}
