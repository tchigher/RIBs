package com.badoo.ribs.sandbox.rib.big

import androidx.lifecycle.Lifecycle
import com.badoo.ribs.clienthelper.interactor.BackStackInteractor
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.sandbox.rib.big.BigRouter.Configuration

class BigInteractor(
    buildParams: BuildParams<*>
) : BackStackInteractor<Big, BigView, Configuration>(
    buildParams = buildParams,
    initialConfiguration = Configuration.Content.Default
) {

    override fun onViewCreated(view: BigView, viewLifecycle: Lifecycle) {
        view.accept(BigView.ViewModel("My id: " + requestCodeClientId.replace("${BigInteractor::class.java.name}.", "")))
    }
}