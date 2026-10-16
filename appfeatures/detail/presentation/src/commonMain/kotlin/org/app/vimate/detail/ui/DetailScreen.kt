package org.app.vimate.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.kmediaplayer.ui.inline.InlineVideoPlayer
import org.app.vimate.appcore.component.Poster
import org.app.vimate.appcore.component.TopBar
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.detail_related
import org.app.vimate.appres.resources.detail_title
import org.app.vimate.detail.ui.component.DetailInfo
import org.app.vimate.detail.ui.constant.DetailLayoutSpec
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DetailScreen(
    mediaId: String,
    autoPlay: Boolean,
    onBack: () -> Unit,
    onOpenDetail: (String) -> Unit,
    viewModel: DetailViewModel = koinViewModel(key = mediaId),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(mediaId) { viewModel.setEvent(DetailContract.Event.Load(mediaId, autoPlay)) }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DetailContract.Effect.OpenDetail -> onOpenDetail(effect.mediaId)
            }
        }
    }
    DetailContent(state, onBack, viewModel::setEvent)
}

@Composable
fun DetailContent(state: DetailContract.State, onBack: () -> Unit = {}, onEvent: (DetailContract.Event) -> Unit = {}) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBar(stringResource(Res.string.detail_title), onBack)
        val detail = state.detail ?: return@Column
        LazyColumn(Modifier.fillMaxSize()) {
            item(key = POSTER_KEY) {
                if (state.videoFiles.isNotEmpty()) {
                    InlineVideoPlayer(state.videoFiles, 0, state.autoPlay)
                } else {
                    Poster(
                        detail.media,
                        Modifier.fillMaxWidth().height(DetailLayoutSpec.posterHeight).clickable { onEvent(DetailContract.Event.Play) },
                    )
                }
            }
            item(key = INFO_KEY) {
                DetailInfo(
                    media = detail.media,
                    following = detail.following,
                    onPlay = { onEvent(DetailContract.Event.Play) },
                    onDownload = { onEvent(DetailContract.Event.Download) },
                    onFollow = { onEvent(DetailContract.Event.ToggleFollow) },
                )
            }
            item(key = RELATED_KEY) {
                Text(
                    stringResource(Res.string.detail_related), style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = DetailLayoutSpec.padding, top = DetailLayoutSpec.gap),
                )
            }
            items(detail.related, key = { it.id }) { related ->
                Text(
                    related.title,
                    modifier = Modifier.fillMaxWidth().clickable { onEvent(DetailContract.Event.OpenRelated(related.id)) }
                        .padding(horizontal = DetailLayoutSpec.padding, vertical = DetailLayoutSpec.relatedPadding),
                )
            }
        }
    }
}

private const val POSTER_KEY = "poster"
private const val INFO_KEY = "info"
private const val RELATED_KEY = "related"
