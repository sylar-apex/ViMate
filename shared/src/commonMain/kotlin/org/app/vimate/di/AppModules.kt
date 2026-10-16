package org.app.vimate.di

import com.psbapp.di.KoinModulesInterface
import com.psbapp.utils.dispatcher.dispatcherDiModule
import org.app.vimate.detail.di.detailDomainModule
import org.app.vimate.home.di.homeDomainModule
import org.app.vimate.onboarding.di.onboardingDomainModule
import org.app.vimate.player.di.playerDomainModule
import org.app.vimate.sharedlogic.di.sharedLogicModule
import org.koin.core.module.Module

/** Every Koin module of the app: core data and one module per feature. */
object AppModules : KoinModulesInterface {
    override fun getModules(): List<Module> = listOf(
        dispatcherDiModule(),
        sharedLogicModule(),
        onboardingDomainModule(),
        playerDomainModule(),
        homeDomainModule(),
        detailDomainModule(),
    )
}
