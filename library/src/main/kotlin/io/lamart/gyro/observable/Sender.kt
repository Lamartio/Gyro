package io.lamart.gyro.observable

import java.util.concurrent.atomic.AtomicReference

interface Sender<T> {
    fun subscribe(receiver: Receiver<T>): Subscription
}

fun <T> senderOf(block: (receiver: Receiver<T>) -> Subscription): Sender<T> =
    object : Sender<T> {
        override fun subscribe(receiver: Receiver<T>): Subscription = block(receiver)
    }

fun <T, R> Sender<T>.map(transform: (T) -> R): Sender<R> =
    wrap { value, receiver ->
        value.let(transform).let(receiver)
    }

fun <T, R> Sender<T>.flatMap(transform: (T) -> Iterable<R>): Sender<R> =
    wrap { value, receiver ->
        value
            .let(transform)
            .fold({}, { l, r -> { l(); receiver(r) } })
            .invoke()
    }

fun <T> Sender<T>.filter(predicate: (T) -> Boolean): Sender<T> =
    wrap { value, receiver ->
        value.takeIf(predicate)?.let(receiver)
    }

@Suppress("UNCHECKED_CAST")
fun <R> Sender<*>.cast(): Sender<R> = wrap { value, receiver -> receiver(value as R) }

inline fun <reified R> Sender<*>.filterCast() = filter(R::class.java::isInstance).cast<R>()

private object UnInitialized

@Suppress("UNCHECKED_CAST")
fun <T> Sender<T>.distinctUntilChanged(equals: (l: T, r: T) -> Boolean = { l, r -> l == r }): Sender<T> =
    lift { receiver ->
        val next = AtomicReference<Any?>(UnInitialized);

        { value ->
            next
                .getAndSet(value)
                ?.takeIf { it == UnInitialized || !equals(it as T, value) }
                ?.let { receiver(value) }
        }
    }

fun <T, R> Sender<T>.wrap(delegate: (value: T, receiver: Receiver<R>) -> Unit): Sender<R> =
    lift { receiver ->
        { value ->
            delegate(value, receiver)
        }
    }

fun <T, R> Sender<T>.lift(transform: (receiver: Receiver<R>) -> Receiver<T>): Sender<R> =
    senderOf { receiver ->
        receiver.let(transform).let(this@lift::subscribe)
    }
