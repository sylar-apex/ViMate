package org.app.vimate.me.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.app.vimate.appcore.adaptive.WideSpec
import org.app.vimate.appcore.component.FolderGlyph
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.sharedlogic.library.Bookmark
import org.app.vimate.sharedlogic.library.BookmarkFolder
import org.app.vimate.sharedlogic.library.BookmarkKind
import org.app.vimate.sharedlogic.library.Bookmarks
import org.app.vimate.sharedlogic.playback.PlaybackLauncher
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/** Bookmarks: nested folders of saved videos, songs and web pages. */
@Composable
fun BookmarksScreen() {
    val nav = LocalAppNavigator.current
    val bm = koinInject<Bookmarks>()
    val folders by bm.folders.items.collectAsState()
    val items by bm.items.items.collectAsState()
    var cur by remember { mutableStateOf<Long?>(null) }
    var dialog by remember { mutableStateOf<FolderDialog?>(null) }
    val open = rememberBookmarkOpener()
    val allLabel = stringResource(Res.string.bm_all)
    // A folder deleted elsewhere drops back to the top level.
    if (cur != null && folders.none { it.id == cur }) cur = null
    val sub = folders.filter { it.parentId == cur }.sortedBy { it.name.lowercase() }
    val here = items.filter { it.folderId == cur }

    Column(Modifier.fillMaxSize().background(ViColors.wideCanvas).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = WideSpec.gutter, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(34.dp).clip(CircleShape).clickable { if (cur != null) cur = folders.firstOrNull { it.id == cur }?.parentId else nav.back() }, contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_m_back, Modifier.size(20.dp), ViColors.ink)
            }
            Breadcrumb(bm.path(cur), Modifier.weight(1f).padding(start = 6.dp)) { cur = it }
            Row(
                Modifier.height(36.dp).clip(RoundedCornerShape(10.dp)).background(VRed).clickable { dialog = FolderDialog.New(cur) }.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Ic(Res.drawable.ic_plus, Modifier.size(16.dp), Color.White)
                Text(stringResource(Res.string.bm_new_folder), Modifier.padding(start = 6.dp), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        LazyVerticalGrid(
            GridCells.Adaptive(220.dp), Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = WideSpec.gutter, end = WideSpec.gutter, bottom = WideSpec.bottomInset),
            horizontalArrangement = Arrangement.spacedBy(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(sub, key = { "f${it.id}" }) { f ->
                FolderTile(f, folders.count { it.parentId == f.id } + items.count { it.folderId == f.id }, { cur = f.id },
                    onRename = { dialog = FolderDialog.Rename(f) }, onDelete = { bm.deleteFolder(f.id) })
            }
            if (sub.isEmpty() && here.isEmpty()) item(span = { GridItemSpan(maxLineSpan) }) {
                Text(stringResource(Res.string.bm_empty), Modifier.padding(vertical = 40.dp), color = ViColors.sectionLabel, fontSize = 14.sp)
            }
            items(here, key = { "b${it.id}" }) { b ->
                BookmarkCard(b, { open(b) }, onRemove = { bm.remove(b.id) },
                    moveTargets = listOfNotNull(cur?.let { c -> folders.firstOrNull { it.id == c } }?.let { f -> f.parentId to "‹ " + (folders.firstOrNull { it.id == f.parentId }?.name ?: allLabel) }) +
                        sub.map { it.id to it.name },
                    onMove = { bm.move(b.id, it) })
            }
        }
    }
    when (val d = dialog) {
        is FolderDialog.New -> NameDialog(stringResource(Res.string.bm_new_folder), "", stringResource(Res.string.bm_create), { dialog = null }) { bm.createFolder(it, d.parent); dialog = null }
        is FolderDialog.Rename -> NameDialog(stringResource(Res.string.bm_rename), d.folder.name, stringResource(Res.string.bm_rename), { dialog = null }) { bm.renameFolder(d.folder.id, it); dialog = null }
        null -> {}
    }
}

private sealed interface FolderDialog {
    data class New(val parent: Long?) : FolderDialog
    data class Rename(val folder: BookmarkFolder) : FolderDialog
}


/** Opens a bookmark: video page, music player or the in-app browser. */
@Composable
fun rememberBookmarkOpener(): (Bookmark) -> Unit {
    val nav = LocalAppNavigator.current
    val launcher = koinInject<PlaybackLauncher>()
    val scope = rememberCoroutineScope()
    return { b ->
        val v = b.videoId?.let { Video(it, b.title, b.subtitle, thumbnail = b.thumbnail.ifEmpty { "https://i.ytimg.com/vi/$it/hqdefault.jpg" }) }
        when {
            v != null && b.kind == BookmarkKind.Music -> scope.launch { runCatching { launcher.audio(listOf(v), 0) } }
            v != null -> nav.go(Routes.Detail.of(v))
            else -> nav.go(Routes.Browser(b.url))
        }
    }
}

@Composable
private fun Breadcrumb(path: List<BookmarkFolder>, modifier: Modifier, onPick: (Long?) -> Unit) {
    LazyRow(modifier, verticalAlignment = Alignment.CenterVertically) {
        item {
            Text(stringResource(Res.string.bm_title), Modifier.clip(RoundedCornerShape(6.dp)).clickable { onPick(null) }.padding(horizontal = 4.dp, vertical = 2.dp),
                color = if (path.isEmpty()) ViColors.ink else ViColors.sectionLabel, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        items(path, key = { it.id }) { f ->
            Text("›", Modifier.padding(horizontal = 4.dp), color = ViColors.sectionLabel, fontSize = 18.sp)
            Text(f.name, Modifier.clip(RoundedCornerShape(6.dp)).clickable { onPick(f.id) }.padding(horizontal = 4.dp, vertical = 2.dp),
                color = if (f == path.last()) ViColors.ink else ViColors.sectionLabel, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

@Composable
private fun FolderTile(f: BookmarkFolder, count: Int, onOpen: () -> Unit, onRename: () -> Unit, onDelete: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        Modifier.fillMaxWidth().height(60.dp).clip(shape).background(ViColors.tileBg).border(1.dp, ViColors.wideHairline, shape).clickable(onClick = onOpen).padding(start = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FolderGlyph(Modifier.size(34.dp, 26.dp))
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(f.name, color = ViColors.ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("$count", color = ViColors.sectionLabel, fontSize = 12.sp)
        }
        MoreMenu(listOf(stringResource(Res.string.bm_rename) to onRename, stringResource(Res.string.bm_delete) to onDelete))
    }
}

@Composable
private fun BookmarkCard(b: Bookmark, onOpen: () -> Unit, onRemove: () -> Unit, moveTargets: List<Pair<Long?, String>>, onMove: (Long?) -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Column(Modifier.fillMaxWidth().clip(shape).background(ViColors.tileBg).border(1.dp, ViColors.wideHairline, shape).clickable(onClick = onOpen)) {
        Box(Modifier.fillMaxWidth().aspectRatio(16 / 9f).background(ViColors.thumbPlaceholder), contentAlignment = Alignment.Center) {
            if (b.thumbnail.isNotEmpty()) AsyncImage(b.thumbnail, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            else Ic(if (b.kind == BookmarkKind.Site) Res.drawable.ic_globe else Res.drawable.ic_m_music, Modifier.size(34.dp), ViColors.sectionLabel)
            Text(b.kind.name, Modifier.align(Alignment.TopStart).padding(6.dp).clip(RoundedCornerShape(4.dp)).background(ViColors.shade40).padding(horizontal = 6.dp, vertical = 2.dp),
                color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
        Row(Modifier.padding(start = 10.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(b.title, color = ViColors.ink, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 17.sp)
                Text(b.subtitle.ifEmpty { b.url.substringAfter("://").substringBefore('/') }, color = ViColors.sectionLabel, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            MoreMenu(listOf(stringResource(Res.string.bm_open) to onOpen) +
                moveTargets.map { (id, name) -> "→ $name" to { onMove(id) } } +
                (stringResource(Res.string.bm_delete) to onRemove))
        }
    }
}

@Composable
private fun MoreMenu(entries: List<Pair<String, () -> Unit>>) {
    var open by remember { mutableStateOf(false) }
    Box {
        Box(Modifier.size(34.dp).clip(CircleShape).clickable { open = true }, contentAlignment = Alignment.Center) {
            Ic(Res.drawable.ic_more_vert, Modifier.size(18.dp), ViColors.ink, stringResource(Res.string.common_more))
        }
        DropdownMenu(open, { open = false }) {
            entries.forEach { (label, action) -> DropdownMenuItem(text = { Text(label) }, onClick = { open = false; action() }) }
        }
    }
}

@Composable
private fun NameDialog(title: String, initial: String, confirm: String, onDismiss: () -> Unit, onDone: (String) -> Unit) {
    var name by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = { OutlinedTextField(name, { name = it }, singleLine = true, placeholder = { Text(stringResource(Res.string.bm_folder_name)) }) },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank()) onDone(name) }) { Text(confirm, color = VRed) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.bm_cancel)) } },
    )
}

/** "Save to bookmarks": compact card with the item, a folder path, folders to step into and an inline new-folder field. */
@Composable
fun BookmarkSaveDialog(item: Bookmark, onDismiss: () -> Unit) {
    val bm = koinInject<Bookmarks>()
    val toast = LocalToast.current
    val folders by bm.folders.items.collectAsState()
    var cur by remember { mutableStateOf<Long?>(null) }
    var naming by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    val all = stringResource(Res.string.bm_all)
    val path = bm.path(cur)
    val savedTo = stringResource(Res.string.bm_saved, path.lastOrNull()?.name ?: all)
    val sub = folders.filter { it.parentId == cur }.sortedBy { it.name.lowercase() }
    fun create() { if (name.isNotBlank()) { cur = bm.createFolder(name, cur); name = ""; naming = false } }
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        val shape = RoundedCornerShape(16.dp)
        Column(Modifier.width(400.dp).clip(shape).background(ViColors.surface).border(1.dp, ViColors.wideHairline, shape)) {
            // Header: title + close
            Row(Modifier.fillMaxWidth().padding(start = 18.dp, end = 8.dp, top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.bm_save), Modifier.weight(1f), color = ViColors.ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Box(Modifier.size(32.dp).clip(CircleShape).clickable(onClick = onDismiss), contentAlignment = Alignment.Center) {
                    Ic(Res.drawable.ic_close, Modifier.size(16.dp), ViColors.sectionLabel)
                }
            }
            // Item preview
            Row(Modifier.padding(horizontal = 18.dp, vertical = 10.dp).fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(ViColors.chipIdle).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(64.dp, 36.dp).clip(RoundedCornerShape(6.dp)).background(ViColors.thumbPlaceholder), contentAlignment = Alignment.Center) {
                    if (item.thumbnail.isNotEmpty()) AsyncImage(item.thumbnail, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else Ic(if (item.kind == BookmarkKind.Site) Res.drawable.ic_globe else Res.drawable.ic_m_music, Modifier.size(18.dp), ViColors.sectionLabel)
                }
                Column(Modifier.weight(1f).padding(start = 10.dp)) {
                    Text(item.title, color = ViColors.ink, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(item.subtitle.ifEmpty { item.url.substringAfter("://").substringBefore('/') }, color = ViColors.sectionLabel, fontSize = 11.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            // Path: tap a crumb to jump back up
            Row(Modifier.fillMaxWidth().padding(start = 14.dp, end = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            LazyRow(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                item { Crumb(all, path.isEmpty()) { cur = null } }
                items(path, key = { it.id }) { f ->
                    Text("›", color = ViColors.sectionLabel, fontSize = 13.sp)
                    Crumb(f.name, f == path.last()) { cur = f.id }
                }
            }
            Row(Modifier.clip(RoundedCornerShape(8.dp)).clickable { naming = true }.padding(horizontal = 8.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                Ic(Res.drawable.ic_plus, Modifier.size(13.dp), VRed)
                Text(stringResource(Res.string.bm_new_folder), Modifier.padding(start = 6.dp), color = VRed, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            }
            // Folders sit in one soft rounded panel; rows separated by hairlines, no full-width dividers.
            LazyColumn(Modifier.padding(horizontal = 18.dp).padding(top = 6.dp).fillMaxWidth().heightIn(max = 240.dp)
                .clip(RoundedCornerShape(12.dp)).background(ViColors.chipIdle).padding(4.dp)) {
                if (naming) item {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        val fr = remember { androidx.compose.ui.focus.FocusRequester() }
                        LaunchedEffect(Unit) { runCatching { fr.requestFocus() } }
                        androidx.compose.foundation.text.BasicTextField(name, { name = it }, Modifier.weight(1f).height(36.dp).focusRequester(fr)
                            .clip(RoundedCornerShape(8.dp)).border(1.dp, VRed, RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 9.dp),
                            singleLine = true, textStyle = androidx.compose.ui.text.TextStyle(color = ViColors.ink, fontSize = 13.5.sp),
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(VRed),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { create() }),
                            decorationBox = { inner -> Box { if (name.isEmpty()) Text(stringResource(Res.string.bm_folder_name), color = ViColors.hint, fontSize = 13.5.sp); inner() } })
                        Text(stringResource(Res.string.bm_create), Modifier.padding(start = 8.dp).clip(RoundedCornerShape(8.dp)).clickable { create() }.padding(horizontal = 10.dp, vertical = 8.dp),
                            color = VRed, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                items(sub, key = { it.id }) { f ->
                    val n = folders.count { it.parentId == f.id }
                    Row(Modifier.fillMaxWidth().height(44.dp).clip(RoundedCornerShape(8.dp)).clickable { cur = f.id }.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        FolderGlyph(Modifier.size(30.dp, 23.dp))
                        Text(f.name, Modifier.weight(1f).padding(start = 10.dp), color = ViColors.ink, fontSize = 13.5.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (n > 0) Text("$n", Modifier.padding(end = 6.dp), color = ViColors.sectionLabel, fontSize = 12.sp)
                        Ic(Res.drawable.ic_chevron, Modifier.size(14.dp).rotate(-90f), ViColors.sectionLabel)
                    }
                }
                if (sub.isEmpty() && !naming) item {
                    Text(stringResource(Res.string.bm_save_here) + " · " + (path.lastOrNull()?.name ?: all), Modifier.padding(10.dp), color = ViColors.sectionLabel, fontSize = 12.5.sp)
                }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.bm_cancel), Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onDismiss).padding(horizontal = 14.dp, vertical = 8.dp),
                    color = ViColors.ink, fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
                Text(stringResource(Res.string.bm_save_here), Modifier.padding(start = 8.dp).clip(RoundedCornerShape(8.dp)).background(VRed)
                    .clickable { bm.save(item, cur); toast(savedTo); onDismiss() }.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun Crumb(label: String, current: Boolean, onClick: () -> Unit) =
    Text(label, Modifier.clip(RoundedCornerShape(6.dp)).clickable(onClick = onClick).padding(horizontal = 4.dp, vertical = 3.dp),
        color = if (current) ViColors.ink else ViColors.sectionLabel, fontSize = 13.sp, fontWeight = if (current) FontWeight.SemiBold else FontWeight.Normal, maxLines = 1)
