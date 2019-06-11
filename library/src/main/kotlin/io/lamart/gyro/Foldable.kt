package io.lamart.gyro

interface Foldable<T> {

    fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R

    companion object {

        fun <T> some(get: () -> T) = object : Foldable<T> {

            override fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R = ifSome(get())

        }

        fun <T> none() = object : Foldable<T> {

            override fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R = ifNone()

        }

        fun <T> maybe(get: () -> T?) = object : Foldable<T> {

            override fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R = get()?.let(ifSome) ?: ifNone()

        }

        fun <T> wrap(get: () -> Foldable<T>) = object : Foldable<T> {

            override fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R = get().fold(ifNone, ifSome)

        }

    }

}

fun <T, R> Foldable<T>.map(transform: (T) -> R): Foldable<R> =
    fold({ Foldable.none() }, { Foldable.some { transform(it) } })

fun <T> Foldable<T>.filter(predicate: (T) -> Boolean): Foldable<T> =
    fold(
        { Foldable.none() },
        { it.takeIf(predicate)?.let { Foldable.some { it } } ?: Foldable.none() }
    )

fun <T> Foldable<T>.getOrElse(default: () -> T): T = fold(default, { it })

fun <T> Foldable<T>.getOrNull(): T? = fold({ null }, { it })

