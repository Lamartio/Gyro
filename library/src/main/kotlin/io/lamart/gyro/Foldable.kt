package io.lamart.gyro

interface Foldable<T> {

    fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R

    companion object {

        fun <T> some(get: () -> T): Foldable<T> = SomeFoldable(get)

        fun <T> none(): Foldable<T> = NoneFoldable()

        fun <T> maybe(get: () -> T?): Foldable<T> = MaybeFoldable(get)

    }

    sealed class Result {
        object None : Result()
        data class Some<T>(val value: T) : Result()
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

fun <T> Foldable<T>.fold(block: (Foldable.Result) -> Unit): Unit =
    fold({ block(Foldable.Result.None) }, { block(Foldable.Result.Some(it)) })

private class SomeFoldable<T>(private val get: () -> T) : Foldable<T> {

    override fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R = ifSome(get())

}

private class NoneFoldable<T> : Foldable<T> {

    override fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R = ifNone()

}

private class MaybeFoldable<T>(private val get: () -> T?) : Foldable<T> {

    override fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R = get()?.let(ifSome) ?: ifNone()

}
