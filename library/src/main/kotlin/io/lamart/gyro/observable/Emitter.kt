package io.lamart.gyro.observable

import com.sun.webkit.dom.RectImpl
import io.lamart.gyro.variables.Variable
import java.util.concurrent.atomic.AtomicBoolean

interface Emitter<T> : Sender<T>, Variable<T> {

    companion object {

        operator fun <T> invoke(value: T, lock: Any = Any()): Emitter<T> =
            EmitterInstance(value, lock)

    }

}

private class EmitterInstance<T>(private var value: T, private val lock: Any) : Emitter<T> {

    private val subscriptions = mutableListOf<SubscriptionInstance>()
    private var publisher: Receiver<T> = {}

    override fun get(): T = synchronized(lock) { value }

    override fun set(value: T) =
        synchronized(lock) {
            this.value = value
            publisher
        }.invoke(value)

    override fun subscribe(receiver: Receiver<T>): Subscription =
        SubscriptionInstance(receiver).also {
            val value = synchronized(lock) {
                subscriptions.add(it)
                invalidatePublisher()
                value
            }

            receiver(value)
        }

    private fun invalidatePublisher() {
        val receiver :Receiver<T> = {  }
        publisher = subscriptions
            .asSequence()
            .map { it.receiver }
            .fold(receiver) { l, r -> { l(it); r(it) } }
    }

    private inner class SubscriptionInstance(receiver: Receiver<T>) : Subscription {

        private val subscribed = AtomicBoolean(true)
        override val isSubscribed: Boolean
            get() = subscribed.get()
        val receiver: Receiver<T> = { value ->
            value
                .takeIf { subscribed.get() }
                ?.let(receiver)
        }

        override fun unsubscribe() {
            synchronized(lock) {
                subscriptions.remove(this)
                subscribed.set(false)
                invalidatePublisher()
            }
        }

    }

}
