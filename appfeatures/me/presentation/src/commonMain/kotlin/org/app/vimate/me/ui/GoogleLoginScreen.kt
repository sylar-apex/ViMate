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
import org.app.vimate.me.domain.constant.MeUrls

/** VidMate's in-app Google sign-in: "Sign in" header over accounts.google.com; closes once YouTube cookies exist. */
@Composable
fun GoogleLoginScreen() {
    val nav = LocalAppNavigator.current
    val toast = LocalToast.current
    val signedIn = stringResource(Res.string.me_signed_in)
    Column(Modifier.fillMaxSize().background(ViColors.white).statusBarsPadding()) {
        PageHeader(stringResource(Res.string.me_sign_in), height = 56) { nav.back() }
        GoogleSignInWeb(MeUrls.GOOGLE_SIGN_IN, Modifier.fillMaxSize()) { toast(signedIn); nav.back() }
    }
}

/** Android: a WebView that presents as mobile Chrome; other platforms open the page in the system browser. */
@Composable
expect fun GoogleSignInWeb(url: String, modifier: Modifier, onSignedIn: () -> Unit)
