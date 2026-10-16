package org.app.vimate.shell

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.vimate.appcore.theme.ViAlpha
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appcore.theme.ViShapes
import org.app.vimate.home.ui.ReportSheet
import org.app.vimate.sharedlogic.event.AppEvents
import org.app.vimate.sharedlogic.event.SheetRequest

/** Shows the bottom sheet any screen asked for through [AppEvents.openSheet]. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SheetHost(events: AppEvents, content: @Composable (SheetRequest) -> Unit) {
    val request by events.sheet.collectAsStateWithLifecycle()
    val active = request ?: return
    val styled = active is SheetRequest.Download || active is SheetRequest.Report
    ModalBottomSheet(
        onDismissRequest = events::closeSheet,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = ViShapes.sheet,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = ViColors.black.copy(alpha = if (styled) ViAlpha.SCRIM_STYLED else ViAlpha.SCRIM),
        dragHandle = if (active is SheetRequest.Report) null else { { BottomSheetDefaults.DragHandle() } },
        contentWindowInsets = { WindowInsets(0) },
    ) {
        content(active)
    }
}

/** Which composable draws each sheet. */
@Composable
internal fun AppSheet(request: SheetRequest) {
    when (request) {
        is SheetRequest.Report -> ReportSheet(request.mediaId)
        is SheetRequest.Download, is SheetRequest.ItemMenu, is SheetRequest.AddToPlaylist, SheetRequest.CreatePlaylist -> Unit
    }
}
