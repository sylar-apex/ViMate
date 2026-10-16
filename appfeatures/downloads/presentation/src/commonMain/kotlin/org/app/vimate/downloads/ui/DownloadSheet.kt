package org.app.vimate.downloads.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.vimate.appcore.component.DurationBadge
import org.app.vimate.appcore.component.MediaThumb
import org.app.vimate.appcore.component.posterColors
import org.app.vimate.appcore.theme.ViAlpha
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appcore.theme.ViShapes
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.dl_download_caps
import org.app.vimate.appres.resources.dl_music
import org.app.vimate.appres.resources.dl_rename
import org.app.vimate.appres.resources.dl_video
import org.app.vimate.appres.resources.ic_download
import org.app.vimate.appres.resources.ic_play
import org.app.vimate.downloads.domain.model.DownloadOptions
import org.app.vimate.downloads.ui.component.PathRow
import org.app.vimate.downloads.ui.component.QualityGrid
import org.app.vimate.downloads.ui.component.SectionMark
import org.app.vimate.downloads.ui.constant.DownloadLayoutSpec
import org.app.vimate.downloads.ui.model.DownloadSheetUiModel
import org.app.vimate.sharedlogic.model.FileKind
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloadSheet(mediaId: String?, url: String?, viewModel: DownloadSheetViewModel = koinViewModel(key = "$mediaId|$url")) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(mediaId, url) { viewModel.setEvent(DownloadSheetContract.Event.Load(mediaId, url)) }
    DownloadSheetContent(state, viewModel::setEvent)
}

@Composable
fun DownloadSheetContent(state: DownloadSheetContract.State, onEvent: (DownloadSheetContract.Event) -> Unit = {}) {
    val options = state.options ?: return
    val model = rememberSaveable(saver = DownloadSheetUiModel.Saver) { DownloadSheetUiModel() }
    LaunchedEffect(options) { model.seed(options) }
    var choice by model.choice
    val card = MaterialTheme.colorScheme.surface
    Column(Modifier.fillMaxWidth().navigationBarsPadding()) {
        Column(
            Modifier.padding(horizontal = DownloadLayoutSpec.sheetSide).fillMaxWidth().clip(ViShapes.panel).background(card).padding(vertical = 12.dp),
        ) {
            NameRow(options, model)
            Spacer(Modifier.height(12.dp))
            PathRow(options.kind, state.path, state.storage, state.folder, state.folderPaths, { onEvent(DownloadSheetContract.Event.SetFolder(it)) }, inset = 12.dp)
        }
        Spacer(Modifier.height(12.dp))
        Column(
            Modifier.padding(horizontal = DownloadLayoutSpec.sheetSide).fillMaxWidth().clip(ViShapes.panel).background(card).padding(bottom = 10.dp),
        ) {
            Row(Modifier.fillMaxWidth().height(DownloadLayoutSpec.sectionRowHeight).padding(start = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                SectionMark(options.kind)
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(if (options.kind == FileKind.MUSIC) Res.string.dl_music else Res.string.dl_video),
                    color = MaterialTheme.colorScheme.onSurface, fontSize = DownloadLayoutSpec.sectionText, fontWeight = FontWeight.Medium,
                )
            }
            QualityGrid(options.choices, choice) { choice = it }
        }
        Spacer(Modifier.height(12.dp))
        DownloadButton { onEvent(DownloadSheetContract.Event.Submit(model.toForm(options))) }
        Spacer(Modifier.height(12.dp))
    }
}

/** Thumbnail, editable file name and "Rename", which focuses the name. */
@Composable
private fun NameRow(options: DownloadOptions, model: DownloadSheetUiModel) {
    var name by model.name
    val focus = remember { FocusRequester() }
    Row(Modifier.padding(horizontal = 12.dp).fillMaxWidth().height(DownloadLayoutSpec.headerHeight)) {
        Box(Modifier.size(DownloadLayoutSpec.thumbWidth, DownloadLayoutSpec.headerHeight).clip(ViShapes.thumb)) {
            val media = options.media
            if (media != null) MediaThumb(media, Modifier.fillMaxSize())
            else Box(Modifier.fillMaxSize().background(Brush.linearGradient(posterColors(options.defaultName))))
            Box(
                Modifier.align(Alignment.Center).size(DownloadLayoutSpec.playCircle).background(ViColors.black.copy(alpha = ViAlpha.SHEET_PLAY_BUTTON), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(painterResource(Res.drawable.ic_play), null, tint = ViColors.white, modifier = Modifier.size(DownloadLayoutSpec.playIcon))
            }
            if (media != null) DurationBadge(media, Modifier.align(Alignment.BottomEnd).padding(end = 4.dp, bottom = 2.dp))
        }
        Spacer(Modifier.width(12.dp))
        BasicTextField(
            value = name,
            onValueChange = { name = it.replace("\n", " ") },
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = DownloadLayoutSpec.nameText, lineHeight = DownloadLayoutSpec.nameLine),
            cursorBrush = SolidColor(ViColors.red),
            maxLines = 3,
            modifier = Modifier.weight(1f).padding(top = 2.dp).focusRequester(focus),
        )
        Spacer(Modifier.width(16.dp))
        Text(
            stringResource(Res.string.dl_rename), color = ViColors.blue, fontSize = DownloadLayoutSpec.linkText,
            modifier = Modifier.align(Alignment.Bottom).padding(bottom = 4.dp).clickable { focus.requestFocus() },
        )
    }
}

@Composable
private fun DownloadButton(onClick: () -> Unit) {
    Row(
        Modifier.padding(horizontal = DownloadLayoutSpec.sheetSide).fillMaxWidth().height(DownloadLayoutSpec.buttonHeight)
            .clip(CircleShape).background(ViColors.redButton).clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(DownloadLayoutSpec.buttonIcon).background(ViColors.white, CircleShape), contentAlignment = Alignment.Center) {
            Icon(painterResource(Res.drawable.ic_download), null, tint = ViColors.redButton, modifier = Modifier.size(DownloadLayoutSpec.buttonGlyph))
        }
        Spacer(Modifier.width(6.dp))
        Text(stringResource(Res.string.dl_download_caps), color = ViColors.white, fontSize = DownloadLayoutSpec.nameText)
    }
}
