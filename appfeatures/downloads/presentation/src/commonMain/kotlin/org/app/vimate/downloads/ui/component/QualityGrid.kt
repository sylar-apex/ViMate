package org.app.vimate.downloads.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.component.RadioDot
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appcore.theme.mutedInk
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.dl_original
import org.app.vimate.appres.resources.dl_unknown_size
import org.app.vimate.downloads.domain.model.DownloadOptions
import org.app.vimate.downloads.domain.model.QualityChoice
import org.app.vimate.downloads.ui.constant.DownloadLayoutSpec
import org.app.vimate.sharedlogic.logic.Formats
import org.jetbrains.compose.resources.stringResource

private const val COLUMNS = 2

/** Two columns of radio choices: quality, container and size; the picked one in red. */
@Composable
internal fun QualityGrid(choices: List<QualityChoice>, selected: String, onSelect: (String) -> Unit) {
    LazyVerticalGrid(
        GridCells.Fixed(COLUMNS),
        Modifier.padding(start = 10.dp).heightIn(max = DownloadLayoutSpec.gridMaxHeight),
        userScrollEnabled = false,
    ) {
        items(choices, key = { it.key }) { choice -> ChoiceCell(choice, choice.key == selected) { onSelect(choice.key) } }
    }
}

@Composable
private fun ChoiceCell(choice: QualityChoice, on: Boolean, onClick: () -> Unit) {
    val ink = MaterialTheme.colorScheme.onSurface
    val tone = if (on) ViColors.dot else mutedInk
    Column(Modifier.height(DownloadLayoutSpec.choiceHeight).clickable(onClick = onClick).padding(end = 10.dp)) {
        Row(Modifier.height(24.dp).padding(top = 1.6.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioDot(on)
            Spacer(Modifier.width(4.dp))
            val label = if (choice.key == DownloadOptions.LINK_KEY) stringResource(Res.string.dl_original) else choice.label
            Text(label, color = if (on) ViColors.dot else ink, fontSize = DownloadLayoutSpec.choiceText, fontWeight = FontWeight.Medium, maxLines = 1)
            Spacer(Modifier.width(2.dp))
            Text("(${choice.format})", color = tone, fontSize = DownloadLayoutSpec.choiceSmall, maxLines = 1)
        }
        Text(
            Formats.sheetSize(choice.bytes).ifEmpty { stringResource(Res.string.dl_unknown_size) },
            color = tone, fontSize = DownloadLayoutSpec.choiceSmall, modifier = Modifier.padding(start = DownloadLayoutSpec.choiceIndent),
        )
    }
}
