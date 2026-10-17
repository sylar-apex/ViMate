package org.app.vimate.sites.di

import org.app.vimate.sites.repository.GrabProbeRepository
import org.app.vimate.sites.repository.GrabProbeRepositoryImpl
import org.app.vimate.sites.usecase.AddSiteVisitUseCase
import org.app.vimate.sites.usecase.EnqueueDirectUseCase
import org.app.vimate.sites.usecase.EnqueueSiteMediaUseCase
import org.app.vimate.sites.usecase.ResolveInstagramPostUseCase
import org.koin.dsl.module

/** Koin bindings of the sites feature (repositories, use cases, view models). */
fun sitesDomainModule() = module {
    single<GrabProbeRepository> { GrabProbeRepositoryImpl() }
    factory { EnqueueDirectUseCase(get()) }
    factory { EnqueueSiteMediaUseCase(get()) }
    factory { ResolveInstagramPostUseCase() }
    factory { AddSiteVisitUseCase(get()) }
}
