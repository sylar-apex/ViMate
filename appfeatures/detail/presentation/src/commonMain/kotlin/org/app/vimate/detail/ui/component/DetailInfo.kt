package org.app.vimate.detail.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appcore.theme.mutedInk
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.detail_following
import org.app.vimate.appres.resources.detail_subscribe
import org.app.vimate.appres.resources.download
import org.app.vimate.appres.resources.play
import org.app.vimate.detail.ui.constant.DetailLayoutSpec
import org.app.vimate.sharedlogic.model.Media
import org.jetbrains.compose.resources.stringResource

/** Title, creator, description, Play and Download, and the follow toggle. */
@Composable
internal fun DetailInfo(media: Media, following: Boolean, onPlay: () -> Unit, onDownload: () -> Unit, onFollow: () -> Unit) {
    Column(Modifier.padding(horizontal = DetailLayoutSpec.padding)) {
        Text(media.title, style = MaterialTheme.typography.titleLarge)
        Text(media.creator, color = mutedInk)
        Text(media.blurb, modifier = Modifier.padding(top = DetailLayoutSpec.gap))
        Row(Modifier.padding(top = DetailLayoutSpec.sectionGap)) {
            Button(onClick = onPlay, colors = ButtonDefaults.buttonColors(containerColor = ViColors.red)) { Text(stringResource(Res.string.play)) }
            Spacer(Modifier.size(DetailLayoutSpec.gap))
            OutlinedButton(onClick = onDownload) { Text(stringResource(Res.string.download)) }
        }
        TextButton(onClick = onFollow) {
            Text(stringResource(if (following) Res.string.detail_following else Res.string.detail_subscribe))
        }
    }
}
