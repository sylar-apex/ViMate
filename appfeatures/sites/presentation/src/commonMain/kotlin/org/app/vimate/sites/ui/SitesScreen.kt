package org.app.vimate.sites.ui

import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.sites.logic.SiteFilter
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.ktor.http.encodeURLParameter
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_globe
import org.app.vimate.appres.resources.sites_add
import org.app.vimate.appres.resources.sites_add_shortcut
import org.app.vimate.appres.resources.sites_add_site
import org.app.vimate.appres.resources.sites_cancel
import org.app.vimate.appres.resources.sites_chip_all
import org.app.vimate.appres.resources.sites_chip_entertainment
import org.app.vimate.appres.resources.sites_chip_music
import org.app.vimate.appres.resources.sites_chip_news
import org.app.vimate.appres.resources.sites_chip_shopping
import org.app.vimate.appres.resources.sites_chip_social
import org.app.vimate.appres.resources.sites_chip_video
import org.app.vimate.appres.resources.sites_empty_filter
import org.app.vimate.appres.resources.sites_entertainment_sub
import org.app.vimate.appres.resources.sites_favorite_sub
import org.app.vimate.appres.resources.sites_group_entertainment
import org.app.vimate.appres.resources.sites_group_favorite
import org.app.vimate.appres.resources.sites_music
import org.app.vimate.appres.resources.sites_music_sub
import org.app.vimate.appres.resources.sites_open
import org.app.vimate.appres.resources.sites_remove
import org.app.vimate.appres.resources.sites_remove_shortcut
import org.app.vimate.appres.resources.sites_search
import org.app.vimate.appres.resources.sites_search_hint
import org.app.vimate.appres.resources.sites_shortcut_name
import org.app.vimate.appres.resources.sites_shortcut_url
import org.app.vimate.appres.resources.sites_subtitle
import org.app.vimate.appres.resources.sites_title
import org.app.vimate.sharedlogic.library.CustomShortcut
import org.app.vimate.sharedlogic.library.Library
import org.app.vimate.sites.logic.typedToUrl
import org.app.vimate.sites.ui.component.SiteLogo
import org.app.vimate.sites.ui.constant.SiteGroup
import org.app.vimate.sites.ui.constant.SiteKind
import org.app.vimate.sites.ui.constant.SiteTile
import org.app.vimate.sites.ui.constant.siteTiles
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import org.app.vimate.sites.ui.constant.SiteTag
import org.jetbrains.compose.resources.StringResource
import org.app.vimate.appres.resources.sites_view_all
import org.app.vimate.appres.resources.sites_wide_subtitle
import org.app.vimate.appres.resources.site_tag_videos
import org.app.vimate.appres.resources.site_tag_shorts
import org.app.vimate.appres.resources.site_tag_playlists
import org.app.vimate.appres.resources.site_tag_reels
import org.app.vimate.appres.resources.site_tag_stories
import org.app.vimate.appres.resources.site_tag_pins
import org.app.vimate.appres.resources.site_tag_search
import org.app.vimate.appres.resources.site_tag_songs
import org.app.vimate.appres.resources.site_tag_albums
import org.app.vimate.appres.resources.site_tag_podcasts
import org.app.vimate.appres.resources.site_tag_talks
import org.app.vimate.appres.resources.site_tag_spotlight
import org.app.vimate.appres.resources.site_tag_posts
import org.koin.compose.koinInject

private val PageBg get() = ViColors.wideCanvas
private val CardLine get() = ViColors.wideHairline
private val Subtitle get() = ViColors.sectionLabel
private val ChipIdle get() = ViColors.chipIdle
private val Accent = Color(0xFFEF3B3B)
private val Gold = Color(0xFFF5B400)
private val Purple = Color(0xFF7C5CFC)

private enum class Chip { All, Social, Video, Music, Entertainment, News, Shopping }

@Composable
fun SitesScreen() {
    val nav = LocalAppNavigator.current
    val library = koinInject<Library>()
    val settings = koinInject<ObservableSettings>()
    val shortcuts by library.customShortcuts.items.collectAsStateWithLifecycle()
    SitesContent(
        shortcuts = shortcuts,
        onBack = { nav.back() },
        onOpen = { nav.go(Routes.Browser(it.url)) },
        onSearch = { q ->
            val url = if (q.contains(' ') || !q.contains('.')) "https://www.google.com/search?q=" + q.encodeURLParameter(spaceToPlus = true)
            else typedToUrl(q) { it.encodeURLParameter(spaceToPlus = true) }
            nav.go(Routes.Browser(if (settings[AppKeys.hideAdultSites]) SiteFilter.safeSearch(url) else url))
        },
        onAdd = { name, url, group ->
            val target = typedToUrl(url.ifBlank { name }) { it.encodeURLParameter(spaceToPlus = true) }
            library.addShortcut(name, target, group.name)
        },
        onRemove = { library.removeShortcut(it.url, it.group) },
    )
}

private sealed interface SiteCell {
    data class BuiltIn(val tile: SiteTile) : SiteCell
    data class Custom(val shortcut: CustomShortcut) : SiteCell
    data object Add : SiteCell
}

@Composable
fun SitesContent(
    shortcuts: List<CustomShortcut> = emptyList(),
    onBack: () -> Unit,
    onOpen: (SiteTile) -> Unit,
    onSearch: (String) -> Unit = {},
    onAdd: (name: String, url: String, group: SiteGroup) -> Unit = { _, _, _ -> },
    onRemove: (CustomShortcut) -> Unit = {},
) {
    var query by remember { mutableStateOf("") }
    var chip by remember { mutableStateOf(Chip.All) }
    var adding by remember { mutableStateOf<SiteGroup?>(null) }
    var removing by remember { mutableStateOf<CustomShortcut?>(null) }
    val kind = chip.toKind()
    BoxWithConstraints(Modifier.fillMaxSize().background(PageBg)) {
        // Desktop / tablets: horizontal brand cards and the search beside the title; phones keep VidMate's tiles.
        val wide = maxWidth >= WIDE_MIN
        LazyVerticalGrid(
            columns = GridCells.Adaptive(if (wide) 230.dp else 112.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 18.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                if (wide) WideHero(query, { query = it }) { submitSearch(query, onSearch); query = "" }
                else SitesHero(query, { query = it }, { submitSearch(query, onSearch); query = "" }, onBack)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                ChipRow(chip) { chip = it }
            }
            val visible = SiteGroup.entries.map { group ->
                group to cellsFor(group, kind, shortcuts)
            }.filter { (_, cells) -> cells.isNotEmpty() }
            if (visible.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(stringResource(Res.string.sites_empty_filter), Modifier.padding(top = 28.dp), color = Subtitle, fontSize = 14.sp)
                }
            }
            visible.forEach { (group, cells) ->
                item(key = "h_$group", span = { GridItemSpan(maxLineSpan) }) {
                    if (wide) WideSectionHeader(group, onAdd = { adding = group }, onViewAll = group.homeKind()?.takeIf { chip == Chip.All }?.let { k -> { chip = Chip.entries.first { it.toKind() == k } } })
                    else SectionHeader(group) { adding = group }
                }
                items(if (wide) cells - SiteCell.Add else cells, key = { cellKey(group, it) }) { cell ->
                    when (cell) {
                        is SiteCell.BuiltIn -> if (wide) WideSiteCard(cell.tile.name.replace('\n', ' '), cell.tile.tags.tagLine(), cell.tile.brand, { onOpen(cell.tile) }) { SiteLogo(cell.tile, 44.dp) }
                            else SiteCard(cell.tile.name, onOpen = { onOpen(cell.tile) }) { SiteLogo(cell.tile, 40.dp) }
                        is SiteCell.Custom -> if (wide) WideSiteCard(cell.shortcut.name, cell.shortcut.url.removePrefix("https://").removePrefix("www."), Purple,
                            { onOpen(SiteTile(cell.shortcut.name, cell.shortcut.url, group = group)) }, onRemove = { removing = cell.shortcut }) { LetterLogo(cell.shortcut.name) }
                        else SiteCard(
                            cell.shortcut.name,
                            onOpen = { onOpen(SiteTile(cell.shortcut.name, cell.shortcut.url, group = group)) },
                            onRemove = { removing = cell.shortcut },
                        ) { LetterLogo(cell.shortcut.name) }
                        SiteCell.Add -> AddCard(if (wide) 72.dp else 112.dp) { adding = group }
                    }
                }
            }
        }
    }
    adding?.let { group ->
        AddShortcutDialog(group, { adding = null }) { name, url -> onAdd(name, url, group); adding = null }
    }
    removing?.let { shortcut ->
        LightDialog(onDismiss = { removing = null }) {
            Text(stringResource(Res.string.sites_remove_shortcut), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ViColors.ink)
            Text(shortcut.name, Modifier.padding(top = 8.dp), color = Subtitle, fontSize = 14.sp)
            DialogActions(
                onCancel = { removing = null },
                confirm = stringResource(Res.string.sites_remove),
                enabled = true,
                onConfirm = { onRemove(shortcut); removing = null },
            )
        }
    }
}

@Composable
private fun SitesHero(query: String, onQuery: (String) -> Unit, onSubmit: () -> Unit, onBack: () -> Unit) {
    Column(Modifier.padding(bottom = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).clip(CircleShape).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_globe, Modifier.size(26.dp), ViColors.ink)
            }
            Column(Modifier.padding(start = 10.dp)) {
                Text(stringResource(Res.string.sites_title), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ViColors.ink, lineHeight = 26.sp)
                Text(stringResource(Res.string.sites_subtitle), fontSize = 13.sp, color = Subtitle, lineHeight = 16.sp)
            }
        }
        SearchBar(query, onQuery, onSubmit, Modifier.padding(top = 16.dp))
    }
}

@Composable
private fun SearchBar(query: String, onQuery: (String) -> Unit, onSubmit: () -> Unit, modifier: Modifier = Modifier) {
    val hint = stringResource(Res.string.sites_search_hint)
    BasicTextField(
        value = query,
        onValueChange = onQuery,
        modifier = modifier.fillMaxWidth().height(48.dp),
        singleLine = true,
        textStyle = TextStyle(fontSize = 14.sp, color = ViColors.ink),
        cursorBrush = SolidColor(Accent),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
        decorationBox = { inner ->
            Row(
                Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)).background(ViColors.surface).border(1.dp, ViColors.searchBorder, RoundedCornerShape(14.dp)).padding(start = 14.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Search, stringResource(Res.string.sites_search), tint = Color(0xFF9AA0A8), modifier = Modifier.size(18.dp))
                Box(Modifier.weight(1f).padding(start = 10.dp, end = 8.dp)) {
                    if (query.isEmpty()) Text(hint, color = Color(0xFFB0B4BA), fontSize = 14.sp, maxLines = 1)
                    inner()
                }
                Box(
                    Modifier.size(width = 46.dp, height = 36.dp).clip(RoundedCornerShape(10.dp)).background(Accent).clickable(onClick = onSubmit),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, stringResource(Res.string.sites_search), tint = ViColors.white, modifier = Modifier.size(18.dp))
                }
            }
        },
    )
}

@Composable
private fun ChipRow(selected: Chip, onSelect: (Chip) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 6.dp)) {
        items(Chip.entries) { chip ->
            val on = chip == selected
            Row(
                Modifier.clip(RoundedCornerShape(20.dp)).background(if (on) Accent else ChipIdle).clickable { onSelect(chip) }.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                chip.icon()?.let { icon ->
                    Icon(icon, null, Modifier.padding(end = 6.dp).size(15.dp), tint = if (on) ViColors.white else ViColors.muted)
                }
                Text(chip.label(), fontSize = 13.sp, fontWeight = if (on) FontWeight.SemiBold else FontWeight.Medium, color = if (on) ViColors.white else ViColors.muted)
            }
        }
    }
}

@Composable
private fun SectionHeader(group: SiteGroup, onAdd: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 10.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SectionMark(group)
            Column(Modifier.padding(start = 10.dp).weight(1f)) {
                Text(group.title(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ViColors.ink)
                Text(group.subtitle(), fontSize = 12.sp, color = Subtitle)
            }
            Row(
                Modifier.clip(RoundedCornerShape(18.dp)).border(1.dp, Accent, RoundedCornerShape(18.dp)).clickable(onClick = onAdd).padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Add, stringResource(Res.string.sites_add_site), Modifier.size(14.dp), tint = Accent)
                Text(stringResource(Res.string.sites_add_site), Modifier.padding(start = 4.dp), color = Accent, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
        Box(Modifier.padding(top = 12.dp).fillMaxWidth().height(1.dp).background(ViColors.wideHairline))
    }
}

@Composable
private fun SectionMark(group: SiteGroup) {
    when (group) {
        SiteGroup.Favorite -> Icon(Icons.Filled.Star, null, Modifier.size(22.dp), tint = Gold)
        SiteGroup.Entertainment -> Box(Modifier.size(26.dp).clip(RoundedCornerShape(8.dp)).background(Purple), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.PlayArrow, null, Modifier.size(16.dp), tint = ViColors.white)
        }
        SiteGroup.Music -> Icon(Icons.Filled.MusicNote, null, Modifier.size(22.dp), tint = Accent)
    }
}

@Composable
private fun SiteCard(name: String, onOpen: () -> Unit, onRemove: (() -> Unit)? = null, logo: @Composable () -> Unit) {
    var menu by remember { mutableStateOf(false) }
    Box(
        Modifier.fillMaxWidth().height(112.dp).clip(RoundedCornerShape(16.dp)).background(ViColors.surface).border(1.dp, CardLine, RoundedCornerShape(16.dp)).clickable(onClick = onOpen),
    ) {
        Box(Modifier.align(Alignment.TopEnd)) {
            Box(Modifier.size(28.dp).clickable { menu = true }, contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.MoreVert, null, Modifier.size(16.dp), tint = ViColors.hint)
            }
            DropdownMenu(menu, { menu = false }, containerColor = ViColors.surface) {
                DropdownMenuItem(text = { Text(stringResource(Res.string.sites_open)) }, onClick = { menu = false; onOpen() })
                if (onRemove != null) DropdownMenuItem(text = { Text(stringResource(Res.string.sites_remove)) }, onClick = { menu = false; onRemove() })
            }
        }
        Column(Modifier.align(Alignment.Center).padding(top = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            logo()
            Text(name, Modifier.padding(top = 8.dp), fontSize = 12.sp, lineHeight = 14.sp, color = ViColors.muted, textAlign = TextAlign.Center, maxLines = 2)
        }
    }
}

@Composable
private fun AddCard(height: Dp, onClick: () -> Unit) {
    val wide = height < 100.dp
    if (wide) return Row(
        Modifier.fillMaxWidth().height(height).clip(RoundedCornerShape(14.dp)).drawBehind {
            drawRoundRect(
                color = Color(0xFFD5D8DE),
                style = Stroke(width = 1.4.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(7.dp.toPx(), 5.dp.toPx()))),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
            )
        }.clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.Add, stringResource(Res.string.sites_add_shortcut), tint = Color(0xFF9AA0A8), modifier = Modifier.size(18.dp))
        Text(stringResource(Res.string.sites_add_site), Modifier.padding(start = 6.dp), fontSize = 13.sp, color = ViColors.muted, fontWeight = FontWeight.Medium)
    }
    Column(
        Modifier.fillMaxWidth().height(height).clip(RoundedCornerShape(16.dp)).drawBehind {
            drawRoundRect(
                color = Color(0xFFD5D8DE),
                style = Stroke(width = 1.4.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(7.dp.toPx(), 5.dp.toPx()))),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
            )
        }.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(Modifier.size(36.dp).clip(CircleShape).background(ViColors.chipIdle), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Add, stringResource(Res.string.sites_add_shortcut), tint = Color(0xFF9AA0A8), modifier = Modifier.size(18.dp))
        }
        Text(stringResource(Res.string.sites_add), Modifier.padding(top = 8.dp), fontSize = 12.sp, color = Color(0xFF9AA0A8))
    }
}

@Composable
private fun LetterLogo(name: String) {
    val letter = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "+"
    val palette = listOf(Color(0xFF5B6CFF), Color(0xFF7C5CFC), Color(0xFF00A86B), Color(0xFFEF3B3B), Color(0xFF1A73E8))
    val index = (name.hashCode() and Int.MAX_VALUE) % palette.size
    Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(palette[index]), contentAlignment = Alignment.Center) {
        Text(letter, color = ViColors.white, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
private fun AddShortcutDialog(group: SiteGroup, onDismiss: () -> Unit, onAdd: (name: String, url: String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = ViColors.ink,
        unfocusedTextColor = ViColors.ink,
        focusedBorderColor = Accent,
        unfocusedBorderColor = CardLine,
        focusedLabelColor = Accent,
        unfocusedLabelColor = Subtitle,
        cursorColor = Accent,
        focusedContainerColor = ViColors.white,
        unfocusedContainerColor = ViColors.white,
    )
    LightDialog(onDismiss = onDismiss) {
        Text(stringResource(Res.string.sites_add_shortcut), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ViColors.ink)
        Text(group.title(), Modifier.padding(top = 4.dp), color = Subtitle, fontSize = 13.sp)
        OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth().padding(top = 16.dp), singleLine = true, shape = RoundedCornerShape(12.dp), label = { Text(stringResource(Res.string.sites_shortcut_name)) }, colors = fieldColors)
        OutlinedTextField(url, { url = it }, Modifier.fillMaxWidth().padding(top = 10.dp), singleLine = true, shape = RoundedCornerShape(12.dp), label = { Text(stringResource(Res.string.sites_shortcut_url)) }, colors = fieldColors)
        DialogActions(onCancel = onDismiss, confirm = stringResource(Res.string.sites_add), enabled = name.isNotBlank()) { onAdd(name.trim(), url.trim()) }
    }
}

@Composable
private fun LightDialog(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.width(420.dp).clip(RoundedCornerShape(16.dp)).background(ViColors.surface).padding(22.dp)) { content() }
    }
}

@Composable
private fun DialogActions(onCancel: () -> Unit, confirm: String, enabled: Boolean, onConfirm: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 18.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = onCancel) { Text(stringResource(Res.string.sites_cancel), color = Subtitle) }
        Box(
            Modifier.padding(start = 8.dp).clip(RoundedCornerShape(10.dp)).background(if (enabled) Accent else Color(0xFFF1D4D4)).clickable(enabled = enabled, onClick = onConfirm).padding(horizontal = 18.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) { Text(confirm, color = ViColors.white, fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }
    }
}

@Composable
private fun Chip.label(): String = stringResource(
    when (this) {
        Chip.All -> Res.string.sites_chip_all
        Chip.Social -> Res.string.sites_chip_social
        Chip.Video -> Res.string.sites_chip_video
        Chip.Music -> Res.string.sites_chip_music
        Chip.Entertainment -> Res.string.sites_chip_entertainment
        Chip.News -> Res.string.sites_chip_news
        Chip.Shopping -> Res.string.sites_chip_shopping
    },
)

private fun Chip.icon(): ImageVector? = when (this) {
    Chip.All -> null
    Chip.Social -> Icons.Filled.Person
    Chip.Video -> Icons.Filled.PlayArrow
    Chip.Music -> Icons.Filled.MusicNote
    Chip.Entertainment -> Icons.Outlined.SportsEsports
    Chip.News -> Icons.Outlined.Article
    Chip.Shopping -> Icons.Outlined.ShoppingBag
}

private fun Chip.toKind(): SiteKind? = when (this) {
    Chip.All -> null
    Chip.Social -> SiteKind.Social
    Chip.Video -> SiteKind.Video
    Chip.Music -> SiteKind.Music
    Chip.Entertainment -> SiteKind.Entertainment
    Chip.News -> SiteKind.News
    Chip.Shopping -> SiteKind.Shopping
}

@Composable
private fun SiteGroup.title(): String = stringResource(
    when (this) {
        SiteGroup.Favorite -> Res.string.sites_group_favorite
        SiteGroup.Entertainment -> Res.string.sites_group_entertainment
        SiteGroup.Music -> Res.string.sites_music
    },
)

@Composable
private fun SiteGroup.subtitle(): String = stringResource(
    when (this) {
        SiteGroup.Favorite -> Res.string.sites_favorite_sub
        SiteGroup.Entertainment -> Res.string.sites_entertainment_sub
        SiteGroup.Music -> Res.string.sites_music_sub
    },
)

private fun SiteGroup.homeKind(): SiteKind? = when (this) {
    SiteGroup.Favorite -> null
    SiteGroup.Entertainment -> SiteKind.Entertainment
    SiteGroup.Music -> SiteKind.Music
}

private fun cellsFor(group: SiteGroup, kind: SiteKind?, shortcuts: List<CustomShortcut>): List<SiteCell> {
    val built = siteTiles.filter { it.group == group && (kind == null || it.kind == kind) }.map { SiteCell.BuiltIn(it) }
    val custom = shortcuts.filter { it.group == group.name && (kind == null || kind == group.homeKind()) }.map { SiteCell.Custom(it) }
    if (kind != null && built.isEmpty()) return emptyList()
    return built + custom + SiteCell.Add
}

private fun submitSearch(query: String, onSearch: (String) -> Unit) {
    val q = query.trim()
    if (q.isNotEmpty()) onSearch(q)
}

private fun cellKey(group: SiteGroup, cell: SiteCell): String = when (cell) {
    is SiteCell.BuiltIn -> "b_${group}_${cell.tile.url}"
    is SiteCell.Custom -> "c_${group}_${cell.shortcut.url}"
    SiteCell.Add -> "add_$group"
}

private val WIDE_MIN = 840.dp

/** Wide header: title and subtitle on the left, the search / link box on the right. */
@Composable
private fun WideHero(query: String, onQuery: (String) -> Unit, onSubmit: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(stringResource(Res.string.sites_title), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = ViColors.ink, lineHeight = 32.sp)
            Text(stringResource(Res.string.sites_wide_subtitle), Modifier.padding(top = 2.dp), fontSize = 14.sp, color = Subtitle)
        }
        SearchBar(query, onQuery, onSubmit, Modifier.padding(start = 24.dp).widthIn(max = 560.dp))
    }
}

/** Section mark, title and subtitle, with + Add Site and View All (jumps to that section's filter chip). */
@Composable
private fun WideSectionHeader(group: SiteGroup, onAdd: () -> Unit, onViewAll: (() -> Unit)?) {
    Row(Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        SectionMark(group)
        Column(Modifier.padding(start = 12.dp).weight(1f)) {
            Text(group.title(), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ViColors.ink)
            Text(group.subtitle(), fontSize = 13.sp, color = Subtitle)
        }
        Row(
            Modifier.clip(RoundedCornerShape(10.dp)).clickable(onClick = onAdd).padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Add, null, Modifier.size(16.dp), tint = Accent)
            Text(stringResource(Res.string.sites_add_site), Modifier.padding(start = 4.dp), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Accent)
        }
        if (onViewAll != null) Row(
            Modifier.padding(start = 6.dp).clip(RoundedCornerShape(10.dp)).background(ViColors.surface).border(1.dp, CardLine, RoundedCornerShape(10.dp)).clickable(onClick = onViewAll)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(Res.string.sites_view_all), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ViColors.ink)
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, Modifier.padding(start = 2.dp).size(18.dp), tint = ViColors.ink)
        }
    }
}

/** Horizontal site card on a light brand tint: logo, name, what it offers and a chevron; custom shortcuts add Remove. */
@Composable
private fun WideSiteCard(name: String, line: String, brand: Color, onOpen: () -> Unit, onRemove: (() -> Unit)? = null, logo: @Composable () -> Unit) {
    var menu by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(14.dp)
    Row(
        Modifier.fillMaxWidth().height(72.dp).clip(shape).background(ViColors.surface).background(brand.copy(alpha = if (ViColors.isDark) 0.14f else 0.06f)).border(1.dp, brand.copy(alpha = 0.12f), shape)
            .clickable(onClick = onOpen).padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        logo()
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ViColors.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (line.isNotEmpty()) Text(line, Modifier.padding(top = 2.dp), fontSize = 12.sp, lineHeight = 15.sp, color = ViColors.muted, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        Box {
            Box(Modifier.size(30.dp).clip(CircleShape).background(ViColors.chipIdle).clickable { if (onRemove != null) menu = true else onOpen() }, contentAlignment = Alignment.Center) {
                Icon(if (onRemove != null) Icons.Filled.MoreVert else Icons.AutoMirrored.Filled.KeyboardArrowRight, null, Modifier.size(18.dp), tint = ViColors.ink)
            }
            DropdownMenu(menu, { menu = false }, containerColor = ViColors.surface) {
                DropdownMenuItem(text = { Text(stringResource(Res.string.sites_open)) }, onClick = { menu = false; onOpen() })
                if (onRemove != null) DropdownMenuItem(text = { Text(stringResource(Res.string.sites_remove)) }, onClick = { menu = false; onRemove() })
            }
        }
    }
}

@Composable
private fun List<SiteTag>.tagLine(): String = map { stringResource(it.label()) }.joinToString(", ")

private fun SiteTag.label(): StringResource = when (this) {
    SiteTag.Videos -> Res.string.site_tag_videos
    SiteTag.Shorts -> Res.string.site_tag_shorts
    SiteTag.Playlists -> Res.string.site_tag_playlists
    SiteTag.Reels -> Res.string.site_tag_reels
    SiteTag.Stories -> Res.string.site_tag_stories
    SiteTag.Pins -> Res.string.site_tag_pins
    SiteTag.Search -> Res.string.site_tag_search
    SiteTag.Songs -> Res.string.site_tag_songs
    SiteTag.Albums -> Res.string.site_tag_albums
    SiteTag.Podcasts -> Res.string.site_tag_podcasts
    SiteTag.Talks -> Res.string.site_tag_talks
    SiteTag.Spotlight -> Res.string.site_tag_spotlight
    SiteTag.Posts -> Res.string.site_tag_posts
}
