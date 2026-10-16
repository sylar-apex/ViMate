package org.app.vimate.onboarding.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.onboarding.ui.constant.OnboardingLayoutSpec

/** One dot per page; the current one is larger and red. */
@Composable
internal fun PageDots(count: Int, current: Int, modifier: Modifier = Modifier) {
    LazyRow(modifier, verticalAlignment = Alignment.CenterVertically, userScrollEnabled = false) {
        items(count, key = { it }) { index ->
            val on = index == current
            Box(
                Modifier.padding(end = OnboardingLayoutSpec.dotGap)
                    .size(if (on) OnboardingLayoutSpec.dotOn else OnboardingLayoutSpec.dotOff)
                    .clip(CircleShape)
                    .background(if (on) ViColors.red else ViColors.line),
            )
        }
    }
}
