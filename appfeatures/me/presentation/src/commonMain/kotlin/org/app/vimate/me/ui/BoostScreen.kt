package org.app.vimate.me.ui

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
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween

/** Phone boost, as VidMate: full green gradient, rocket in two translucent rings, "Freeing up" + counting percent. */
@Composable
fun BoostScreen(viewModel: BoostViewModel = koinViewModel()) {
    val nav = LocalAppNavigator.current
    val pct = viewModel.uiState.collectAsStateWithLifecycle().value.pct
    val lift by rememberInfiniteTransition(label = "boost").animateFloat(0f, -10f, infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "lift")
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(ViColors.meBoostTop, ViColors.meBoostBottom)))) {
        Row(Modifier.statusBarsPadding().height(54.dp).padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.padding(start = 10.dp).size(40.dp).clickable { nav.back() }, contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_back, Modifier.size(30.dp), ViColors.white, stringResource(Res.string.me_back)) }
            Text(stringResource(Res.string.me_phone_boost), Modifier.padding(start = 24.dp), color = ViColors.white, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        }
        Box(Modifier.padding(top = 204.dp).size(244.dp).align(Alignment.TopCenter).border(1.dp, ViColors.meRing, CircleShape), contentAlignment = Alignment.Center) {
            Box(Modifier.size(198.dp).clip(CircleShape).background(ViColors.meRing).border(1.dp, ViColors.meRingEdge, CircleShape), contentAlignment = Alignment.Center) {
                Svg("rocket", Modifier.offset(y = lift.dp).size(110.dp).rotate(-45f))
            }
        }
        Column(Modifier.align(Alignment.TopCenter).padding(top = 490.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(if (pct < 100) Res.string.me_freeing_up else Res.string.me_optimized), color = ViColors.white, fontSize = 15.sp)
            Text(stringResource(Res.string.me_percent, pct), color = ViColors.white, fontSize = 52.sp, lineHeight = 60.sp, fontWeight = FontWeight.Bold)
        }
    }
}
