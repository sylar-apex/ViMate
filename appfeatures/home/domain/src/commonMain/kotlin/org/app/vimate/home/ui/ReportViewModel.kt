package org.app.vimate.home.ui

import com.psbapp.mvi.MVIBaseViewModel
import org.app.vimate.sharedlogic.usecase.ReportMediaUseCase

/** Saves the reason picked on the report sheet. */
class ReportViewModel(
    private val report: ReportMediaUseCase,
) : MVIBaseViewModel<ReportContract.Event, ReportContract.State, ReportContract.Effect>() {
    override fun createInitialState() = ReportContract.State
    override fun handleEffect(effect: ReportContract.Effect) {}

    override fun handleEvent(event: ReportContract.Event) {
        when (event) {
            is ReportContract.Event.Submit -> report(event.mediaId, event.reason)
        }
    }
}
