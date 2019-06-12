package io.lamart.gyro

import java.util.concurrent.atomic.AtomicReference

interface Segment<T> : Variable<T>, Foldable<T> {

    fun <R> map(get: T.() -> R, copy: T.(R) -> T): Segment<R>

    fun filter(predicate: (T) -> Boolean): ConditionalSegment<T>

    fun <R> filter(type: Class<R>): ConditionalSegment<R>

    fun cast(): ConditionalSegment<T>

}

inline fun <reified R> Segment<*>.filter() = filter(R::class.java)

fun <T> AtomicReference<T>.toSegment() = segmentOf(::get, ::set)

fun <T> segmentOf(get: () -> T, set: (T) -> Unit): Segment<T> =
    object : Segment<T>,
        Variable<T> by Variable(get, set),
        Foldable<T> by Foldable.some(get) {

        override fun <R> map(get: T.() -> R, copy: T.(R) -> T): Segment<R> =
            segmentOf(
                { get().let(get) },
                { copy(get(), it).let(::set) }
            )

        override fun filter(predicate: (T) -> Boolean): ConditionalSegment<T> =
            conditionalSegmentOf({ Foldable.some(get).filter(predicate) }, set)

        @Suppress("UNCHECKED_CAST")
        override fun <R> filter(type: Class<R>): ConditionalSegment<R> =
            conditionalSegmentOf(
                { Foldable.some(get).filter(type::isInstance).map { it as R } },
                { set(it as T) }
            )

        override fun cast(): ConditionalSegment<T> = conditionalSegmentOf({ Foldable.some(get) }, set)

    }