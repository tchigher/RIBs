package com.badoo.ribs.core.routing.configuration.action.single

import android.os.Parcelable
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.routing.activator.RoutingActivator
import com.badoo.ribs.core.routing.configuration.RoutingContext.Resolved
import com.badoo.ribs.core.routing.configuration.action.ActionExecutionParams
import com.badoo.ribs.core.routing.configuration.feature.ConfigurationFeature.Effect.Individual.PendingRemovalTrue
import com.badoo.ribs.core.routing.configuration.feature.ConfigurationFeature.Effect.Individual.Removed
import com.badoo.ribs.core.routing.configuration.feature.EffectEmitter
import com.badoo.ribs.core.routing.history.Routing
import com.badoo.ribs.core.routing.transition.TransitionElement

/**
 * Removes [Node]s from their parent, resulting in the end of their lifecycles.
 */
internal class RemoveAction<C : Parcelable>(
    private val emitter: EffectEmitter<C>,
    private val routing: Routing<C>,
    private var item: Resolved<C>,
    private val activator: RoutingActivator<C>
) : RoutingTransitionAction<C> {

    object Factory: ActionFactory {
        override fun <C : Parcelable> create(params: ActionExecutionParams<C>): RoutingTransitionAction<C> =
            RemoveAction(
                emitter = params.transactionExecutionParams.emitter,
                routing = params.routing,
                item = params.item,
                activator = params.transactionExecutionParams.activator
            )
    }

    override var canExecute: Boolean =
        true

    override var transitionElements: List<TransitionElement<C>> =
        emptyList()

    override fun onBeforeTransition() {
    }

    override fun onTransition(forceExecute: Boolean) {
        activator.onTransitionRemove(routing, item.nodes)
        emitter.onNext(PendingRemovalTrue(routing))
    }

    override fun onFinish(forceExecute: Boolean) {
        activator.remove(routing, item.nodes)
        emitter.onNext(Removed(routing, item))
    }
}
