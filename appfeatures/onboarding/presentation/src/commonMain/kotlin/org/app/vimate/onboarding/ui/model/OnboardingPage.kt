package org.app.vimate.onboarding.ui.model

import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.onboarding_cleaner_body
import org.app.vimate.appres.resources.onboarding_cleaner_title
import org.app.vimate.appres.resources.onboarding_faster_body
import org.app.vimate.appres.resources.onboarding_faster_title
import org.app.vimate.appres.resources.onboarding_smarter_body
import org.app.vimate.appres.resources.onboarding_smarter_title
import org.jetbrains.compose.resources.StringResource

/** Text of one intro page; the poster gradient is picked from [key]. */
data class OnboardingPage(val key: String, val title: StringResource, val body: StringResource)

val onboardingPages = listOf(
    OnboardingPage("Smarter", Res.string.onboarding_smarter_title, Res.string.onboarding_smarter_body),
    OnboardingPage("Cleaner", Res.string.onboarding_cleaner_title, Res.string.onboarding_cleaner_body),
    OnboardingPage("Faster", Res.string.onboarding_faster_title, Res.string.onboarding_faster_body),
)
