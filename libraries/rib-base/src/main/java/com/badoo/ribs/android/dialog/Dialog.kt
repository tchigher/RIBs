package com.badoo.ribs.android.dialog

import com.badoo.ribs.android.text.Text
import com.badoo.ribs.core.ActivationMode
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.builder.BuildContext
import com.badoo.ribs.core.builder.NodeFactory
import com.badoo.ribs.android.dialog.Dialog.CancellationPolicy.NonCancellable
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource

abstract class Dialog<T : Any> private constructor(
    factory: Dialog<T>.() -> Unit,
    private val events: PublishRelay<T>
) : ObservableSource<T> by events {
    var title: Text? = null
    var message: Text? = null
    var cancellationPolicy: CancellationPolicy<T> = NonCancellable()
    internal var buttons: ButtonsConfig<T>? = null
    private var nodeFactory: NodeFactory? = null
    internal var rib: Rib? = null

    constructor(factory: Dialog<T>.() -> Unit) : this(
        factory,
        PublishRelay.create()
    )

    init {
        factory()
    }

    fun nodeFactory(nodeFactory: NodeFactory) {
        this.nodeFactory = nodeFactory
    }

    fun buttons(factory: ButtonsConfig<T>.() -> Unit) {
        this.buttons = ButtonsConfig(factory)
    }

    class ButtonsConfig<T : Any>(
        factory: ButtonsConfig<T>.() -> Unit
    ) {
        internal var positive: ButtonConfig<T>? = null
        internal var negative: ButtonConfig<T>? = null
        internal var neutral: ButtonConfig<T>? = null

        init {
            factory()
        }

        fun positive(title: Text, onClickEvent: T, closesDialogAutomatically: Boolean = true) {
            positive = ButtonConfig(title, onClickEvent, closesDialogAutomatically)
        }

        fun negative(title: Text, onClickEvent: T, closesDialogAutomatically: Boolean = true) {
            negative = ButtonConfig(title, onClickEvent, closesDialogAutomatically)
        }

        fun neutral(title: Text, onClickEvent: T, closesDialogAutomatically: Boolean = true) {
            neutral = ButtonConfig(title, onClickEvent, closesDialogAutomatically)
        }

        data class ButtonConfig<T : Any>(
            var title: Text? = null,
            var onClickEvent: T? = null,
            var closesDialogAutomatically: Boolean = true
        )
    }

    fun publish(event: T) {
        events.accept(event)
    }

    fun buildNodes(buildContext: BuildContext): List<Rib> =
        nodeFactory?.let { factory ->
            val clientParams = buildContext.copy(
                /**
                 * Children inside dialogs behaved like root nodes so far in that they were
                 * not added as a child of any other Node.
                 * Using [AncestryInfo.Child.anchor] it's now also possible to change this,
                 * and rather add the child inside the dialog to the parent, if removal is also guaranteed.
                 * A benefit of this would be back press and lifecycle propagation.
                 * Not entirely sure it is needed. To be reconsidered later.
                 */
                activationMode = ActivationMode.NOOP
            )

            listOf(
                factory.invoke(clientParams).also {
                    rib = it
                }
            )
        } ?: emptyList()

    sealed class Event {
        object Positive : Event()
        object Negative : Event()
        object Neutral : Event()
        object Cancelled : Event()
    }

    sealed class CancellationPolicy<T : Any> {
        class NonCancellable<T : Any> : CancellationPolicy<T>()
        data class Cancellable<T : Any>(
            val event: T,
            val cancelOnTouchOutside: Boolean
        ) : CancellationPolicy<T>()
    }
}
