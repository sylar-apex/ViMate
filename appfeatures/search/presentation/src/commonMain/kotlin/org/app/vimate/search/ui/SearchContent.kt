package org.app.vimate.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.search.constant.SearchTab
import org.app.vimate.search.ui.component.MusicResults
import org.app.vimate.search.ui.component.SearchHome
import org.app.vimate.search.ui.component.SearchResults
import org.app.vimate.search.ui.component.SearchTabs
import org.app.vimate.search.viewmodel.SearchContract.Event
import org.app.vimate.search.viewmodel.SearchContract.State
import org.jetbrains.compose.resources.stringResource

// Measured from VidMate: 50dp header, 32dp back chevron, 216x31dp white field, bold ENTER.
@Composable
fun SearchContent(state: State, autoFocus: Boolean, onEvent: (Event) -> Unit) {
    val nav = LocalAppNavigator.current
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { if (autoFocus) runCatching { focus.requestFocus() } }

    Column(Modifier.fillMaxSize().background(ViColors.searchBg).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(50.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.padding(start = 12.dp).size(32.dp).clickable { nav.back() }, contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_back, Modifier.size(28.dp), ViColors.ink, stringResource(Res.string.search_back))
            }
            Row(Modifier.weight(1f).padding(end = 12.dp).height(36.dp).clip(CircleShape).background(ViColors.surface).padding(start = 13.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (state.query.isEmpty()) Text(stringResource(Res.string.search_hint), color = ViColors.hint, fontSize = 15.sp, lineHeight = 19.sp, maxLines = 1)
                    BasicTextField(state.query, { onEvent(Event.QueryChanged(it)) }, Modifier.fillMaxWidth().focusRequester(focus), singleLine = true,
                        textStyle = TextStyle(fontSize = 15.sp, color = ViColors.ink),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onSearch = { onEvent(Event.Submit(state.query)) }))
                }
                // Clear text (replaces VidMate's ENTER; search runs from the keyboard).
                if (state.query.isNotEmpty()) Box(Modifier.size(28.dp).clip(CircleShape).clickable { onEvent(Event.ClearQuery); runCatching { focus.requestFocus() } }, contentAlignment = Alignment.Center) {
                    Box(Modifier.size(20.dp).clip(CircleShape).background(ViColors.clearCircle), contentAlignment = Alignment.Center) {
                        Ic(Res.drawable.ic_close, Modifier.size(13.dp), Color.White, stringResource(Res.string.search_clear))
                    }
                }
            }
        }
        when {
            state.suggestions.isNotEmpty() -> LazyColumn {
                items(state.suggestions, key = { it }) { s ->
                    ListItem(headlineContent = { Text(s) }, leadingContent = { Icon(Icons.Outlined.Search, null) },
                        modifier = Modifier.clickable { onEvent(Event.Submit(s)) })
                }
            }
            state.submitted.isEmpty() -> SearchHome(state.history, onPick = { onEvent(Event.Submit(it)) }, onClear = { onEvent(Event.ClearHistory) })
            else -> {
                SearchTabs(state.tab, onTab = { onEvent(Event.SelectTab(it)) })
                when (state.tab) {
                    SearchTab.Music -> MusicResults(state, onEvent)
                    SearchTab.All, SearchTab.Playlist -> SearchResults(state, onEvent)
                    else -> {}
                }
            }
        }
    }
}
