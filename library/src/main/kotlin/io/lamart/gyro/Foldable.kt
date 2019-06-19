package io.lamart.gyro

interface Foldable<T> {

    fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R

    companion object {

        fun <T> some(get: () -> T): Foldable<T> = SomeFoldable(get)

        fun <T> none(): Foldable<T> = NoneFoldable()

        fun <T> maybe(get: () -> T?): Foldable<T> = MaybeFoldable(get)

        fun <T> wrap(get: () -> Foldable<T>): Foldable<T> = WrapFoldable(get)

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

fun <T> Foldable<T>.fold(block: (FoldResult) -> Unit): Unit =
    fold({ block(FoldResult.None) }, { block(FoldResult.Some(it)) })

sealed class FoldResult {
    object None : FoldResult()
    data class Some<T>(val value: T) : FoldResult()
}

private class SomeFoldable<T>(private val get: () -> T) : Foldable<T> {

    override fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R = ifSome(get())

}

private class NoneFoldable<T> : Foldable<T> {

    override fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R = ifNone()

}

private class MaybeFoldable<T>(private val get: () -> T?) : Foldable<T> {

    override fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R = get()?.let(ifSome) ?: ifNone()

}

private class WrapFoldable<T>(private val get: () -> Foldable<T>) : Foldable<T> {

    override fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R = get().fold(ifNone, ifSome)

}
