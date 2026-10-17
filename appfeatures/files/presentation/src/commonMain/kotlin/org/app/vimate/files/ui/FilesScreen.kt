package org.app.vimate.files.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.adaptive.LocalWindowClass
import org.app.vimate.appcore.adaptive.WindowClass
import org.app.vimate.appcore.component.BackBar
import org.app.vimate.files.ui.wide.FilesWide
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.VMenu
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.files_delete
import org.app.vimate.appres.resources.files_help
import org.app.vimate.appres.resources.files_junk
import org.app.vimate.appres.resources.files_more
import org.app.vimate.appres.resources.files_pause_all
import org.app.vimate.appres.resources.files_private
import org.app.vimate.appres.resources.files_resume_all
import org.app.vimate.appres.resources.files_search
import org.app.vimate.appres.resources.files_setting
import org.app.vimate.appres.resources.files_storage
import org.app.vimate.appres.resources.files_tab_downloads
import org.app.vimate.appres.resources.files_tab_music
import org.app.vimate.appres.resources.files_tab_private
import org.app.vimate.appres.resources.files_tab_videos
import org.app.vimate.appres.resources.files_title
import org.app.vimate.appres.resources.ic_junk
import org.app.vimate.appres.resources.ic_lock
import org.app.vimate.appres.resources.ic_m_help
import org.app.vimate.appres.resources.ic_m_pausec
import org.app.vimate.appres.resources.ic_m_resume
import org.app.vimate.appres.resources.ic_m_search
import org.app.vimate.appres.resources.ic_m_setting
import org.app.vimate.appres.resources.ic_m_storage
import org.app.vimate.appres.resources.ic_m_trash
import org.app.vimate.appres.resources.ic_more_thin
import org.app.vimate.files.ui.component.DownloadsTab
import org.app.vimate.files.ui.component.LocalTab
import org.app.vimate.files.ui.component.PrivateGate
import org.app.vimate.files.usecase.FilesUseCase
import org.app.vimate.sharedlogic.download.vid.fmtSize
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private val fileTabs = listOf(Res.string.files_tab_downloads, Res.string.files_tab_videos, Res.string.files_tab_music, Res.string.files_tab_private)
private const val PRIVATE_TAB = 3

/** POC FilesScreen: "My Files" header (junk + menu), Downloads / Videos / Music / Private tabs. */
@Composable
fun FilesScreen() {
    if (LocalWindowClass.current != WindowClass.Compact) return FilesWide()
    val nav = LocalAppNavigator.current
    val files = koinInject<FilesUseCase>()
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var top by remember { mutableStateOf(false) }
    var selecting by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        // Header 50dp: title at x=16, 32dp junk icon at x=272, 40dp menu at x=310 (measured from VidMate).
        Row(Modifier.fillMaxWidth().height(50.dp).padding(start = 16.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.files_title), fontSize = 19.2.sp, lineHeight = 27.sp, fontWeight = FontWeight.Bold, color = ViColors.ink, modifier = Modifier.weight(1f))
            Box(Modifier.size(32.dp).clickable { nav.go(Routes.JunkClean) }, contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_junk, Modifier.size(28.dp), ViColors.ink, stringResource(Res.string.files_junk)) }
            if (tab != PRIVATE_TAB) Box(Modifier.padding(start = 6.dp).size(40.dp)) {
                Box(Modifier.fillMaxSize().clickable { top = true }, contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_more_thin, Modifier.size(22.dp), ViColors.ink, stringResource(Res.string.files_more)) }
                val stat = remember { files.storage() }
                VMenu(top, { top = false }, 221, listOf(
                    Triple(Res.drawable.ic_m_search, stringResource(Res.string.files_search)) { nav.go(Routes.Search()) },
                    Triple(Res.drawable.ic_m_setting, stringResource(Res.string.files_setting)) { nav.go(Routes.SettingsGroup("Download")) },
                    Triple(Res.drawable.ic_m_pausec, stringResource(Res.string.files_pause_all)) { files.downloads.pauseAll() },
                    Triple(Res.drawable.ic_m_resume, stringResource(Res.string.files_resume_all)) { files.downloads.resumeAll() },
                    Triple(Res.drawable.ic_m_trash, stringResource(Res.string.files_delete)) { tab = 0; selecting = true },
                    Triple(Res.drawable.ic_m_storage, stringResource(Res.string.files_storage, fmtSize(stat.usedBytes), fmtSize(stat.totalBytes))) {},
                    Triple(Res.drawable.ic_m_help, stringResource(Res.string.files_help)) { nav.go(Routes.Help) },
                ), offset = DpOffset(0.dp, (-40).dp))
            }
        }
        // Tabs 38dp, 17dp side padding, 3dp black indicator inset 10dp, 1dp #C3C3C3 rule.
        Box(Modifier.fillMaxWidth().height(39.dp)) {
            Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().height(1.dp).background(ViColors.hairline))
            LazyRow(Modifier.fillMaxHeight(), userScrollEnabled = false) {
                itemsIndexed(fileTabs, key = { i, _ -> i }) { i, t ->
                    val sel = tab == i
                    val private = i == PRIVATE_TAB
                    Box(Modifier.fillMaxHeight().clickable { tab = i }) {
                        Row(Modifier.fillMaxHeight().padding(start = if (private) 10.dp else 17.dp, end = 17.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (private) Ic(Res.drawable.ic_lock, Modifier.padding(end = 4.dp).offset(y = (-1.3).dp).size(17.3.dp), if (sel) ViColors.black else ViColors.tabGrey)
                            Text(stringResource(t), fontSize = if (sel) 13.6.sp else 13.5.sp, lineHeight = 19.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                color = if (sel) ViColors.black else ViColors.tabGrey)
                        }
                        if (sel) Box(Modifier.matchParentSize().padding(start = 10.dp, end = 10.dp, bottom = 1.dp).wrapContentHeight(Alignment.Bottom).height(3.dp).background(ViColors.black))
                    }
                }
            }
        }
        when (tab) {
            0 -> DownloadsTab(selecting) { selecting = it }
            1 -> LocalTab(audio = false)
            2 -> LocalTab(audio = true)
            else -> PrivateGate()
        }
    }
}

/** POC PrivateScreen: the Private folder as its own page (Routes.Private). */
@Composable
fun PrivateScreen() {
    val nav = LocalAppNavigator.current
    Column(Modifier.fillMaxSize().statusBarsPadding()) { BackBar(stringResource(Res.string.files_private), { nav.back() }); PrivateGate() }
}
