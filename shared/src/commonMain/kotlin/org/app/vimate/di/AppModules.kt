package org.app.vimate.di

import com.psbapp.di.KoinModulesInterface
import com.psbapp.utils.dispatcher.dispatcherDiModule
import org.app.vimate.batch.di.batchDomainModule
import org.app.vimate.detail.di.detailDomainModule
import org.app.vimate.downloads.di.downloadsDomainModule
import org.app.vimate.files.di.filesDomainModule
import org.app.vimate.home.di.homeDomainModule
import org.app.vimate.me.di.meDomainModule
import org.app.vimate.music.di.musicDomainModule
import org.app.vimate.player.di.playerDomainModule
import org.app.vimate.search.di.searchDomainModule
import org.app.vimate.settings.di.settingsDomainModule
import org.app.vimate.sharedlogic.di.sharedLogicModule
import org.app.vimate.shorts.di.shortsDomainModule
import org.app.vimate.sites.di.sitesDomainModule
import org.koin.core.module.Module

/** Every Koin module of the app: core data and one module per feature. */
object AppModules : KoinModulesInterface {
    override fun getModules(): List<Module> = listOf(
        dispatcherDiModule(),
        sharedLogicModule(),
        playerDomainModule(),
        homeDomainModule(),
        searchDomainModule(),
        detailDomainModule(),
        shortsDomainModule(),
        sitesDomainModule(),
        musicDomainModule(),
        batchDomainModule(),
        filesDomainModule(),
        meDomainModule(),
        settingsDomainModule(),
        downloadsDomainModule(),
    )
}
