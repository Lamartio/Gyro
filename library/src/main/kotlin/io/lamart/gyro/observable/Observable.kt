package io.lamart.gyro.observable

import io.lamart.gyro.variables.Variable

interface Observable<T> : Variable<T> {

    fun subscribe(observer: Observer<T>): Subscription

    companion object {

        operator fun <T> invoke(
            value: T,
            lock: Any = Any(),
            afterSubscribe: (value: T, observer: Observer<T>) -> Unit = { value, observer -> observer(value) }
        ): Observable<T> = ObservableInstance(value, lock, afterSubscribe)

    }

}

private class ObservableInstance<T>(
    private var value: T,
    private val lock: Any,
    private val afterSubscribe: (value: T, observer: (T) -> Unit) -> Unit
) : Observable<T> {

    private val subscriptions = mutableListOf<SubscriptionInstance>()
    private var publisher: Observer<T> = {}

    override fun get(): T = synchronized(lock) { value }

    override fun set(value: T) =
        synchronized(lock) {
            this.value = value
            publisher
        }.invoke(value)

    override fun subscribe(observer: Observer<T>): Subscription =
        SubscriptionInstance(observer).also {
            val value = synchronized(lock) {
                subscriptions.add(it)
                invalidatePublisher()
                value
            }

            afterSubscribe(value, observer)
        }

    private fun invalidatePublisher() {
        publisher = subscriptions
            .asSequence()
            .map { it.observer }
            .reduce { l, r -> { l(it); r(it) } }
    }

    private inner class SubscriptionInstance(val observer: Observer<T>) : Subscription {

        override fun unsubscribe() {
            synchronized(lock) {
                subscriptions.remove(this)
                invalidatePublisher()
            }
        }

    }

}
