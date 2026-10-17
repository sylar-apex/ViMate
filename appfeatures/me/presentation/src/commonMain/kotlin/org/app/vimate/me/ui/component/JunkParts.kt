package org.app.vimate.me.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.vimate.appcore.component.*
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.me.ui.component.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.app.vimate.me.domain.model.JunkKind

@Composable
internal fun JunkSize(t: String) = Text(t, Modifier.padding(end = 8.dp), fontSize = 12.sp, color = ViColors.meGrey)

@Composable
internal fun JunkCheck(on: Boolean, toggle: () -> Unit) {
    Box(Modifier.size(20.dp).clickable(onClick = toggle), contentAlignment = Alignment.Center) {
        Ic(if (on) Res.drawable.ic_j_check else Res.drawable.ic_j_circle, Modifier.size(20.dp), ViColors.meRed)
    }
}

internal val JunkKind.label get() = when (this) {
    JunkKind.CACHE -> Res.string.me_junk_cache; JunkKind.TRASH -> Res.string.me_junk_trash; JunkKind.TEMP -> Res.string.me_junk_temp
    JunkKind.THUMBNAIL -> Res.string.me_junk_thumbnail; JunkKind.AD -> Res.string.me_junk_ad
}
internal val JunkKind.icon get() = when (this) {
    JunkKind.CACHE -> Res.drawable.ic_j_trash; JunkKind.TRASH -> Res.drawable.ic_j_recycle; JunkKind.TEMP -> Res.drawable.ic_j_clock
    JunkKind.THUMBNAIL -> Res.drawable.ic_j_image; JunkKind.AD -> Res.drawable.ic_j_ad
}
internal val JunkKind.tint get() = when (this) {
    JunkKind.CACHE -> ViColors.meRed; JunkKind.TRASH, JunkKind.THUMBNAIL -> ViColors.meJunkGreen; JunkKind.TEMP -> ViColors.meJunkBlue; JunkKind.AD -> ViColors.meFeatureAmber
}
