package org.app.vimate.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.vimate.appcore.component.posterColors
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appcore.theme.ViShapes
import org.app.vimate.appcore.theme.mutedInk
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.onboarding_next
import org.app.vimate.appres.resources.onboarding_skip
import org.app.vimate.appres.resources.onboarding_start
import org.app.vimate.onboarding.domain.constant.OnboardingLimits
import org.app.vimate.onboarding.ui.component.PageDots
import org.app.vimate.onboarding.ui.constant.OnboardingLayoutSpec
import org.app.vimate.onboarding.ui.model.onboardingPages
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    OnboardingContent(
        state = state,
        onNext = { viewModel.setEvent(OnboardingContract.Event.Next) },
        onSkip = { viewModel.setEvent(OnboardingContract.Event.Skip) },
    )
}

@Composable
fun OnboardingContent(state: OnboardingContract.State, onNext: () -> Unit = {}, onSkip: () -> Unit = {}) {
    val page = onboardingPages[state.page]
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding().padding(OnboardingLayoutSpec.screenPadding),
    ) {
        if (!state.isLast) {
            Text(stringResource(Res.string.onboarding_skip), modifier = Modifier.align(Alignment.End).clickable(onClick = onSkip), color = mutedInk)
        } else {
            Spacer(Modifier.height(OnboardingLayoutSpec.skipSpacer))
        }
        Spacer(Modifier.weight(1f))
        Spacer(
            Modifier.fillMaxWidth().height(OnboardingLayoutSpec.posterHeight).clip(ViShapes.card)
                .background(Brush.verticalGradient(posterColors(page.key))),
        )
        Spacer(Modifier.height(OnboardingLayoutSpec.sectionGap))
        Text(stringResource(page.title), style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(page.body), color = mutedInk, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(OnboardingLayoutSpec.sectionGap))
        Row(verticalAlignment = Alignment.CenterVertically) {
            PageDots(OnboardingLimits.PAGE_COUNT, state.page, Modifier.weight(1f))
            Button(onClick = onNext, colors = ButtonDefaults.buttonColors(containerColor = ViColors.red), shape = ViShapes.pill) {
                Text(stringResource(if (state.isLast) Res.string.onboarding_start else Res.string.onboarding_next))
            }
        }
        Spacer(Modifier.height(OnboardingLayoutSpec.bottomGap))
    }
}
