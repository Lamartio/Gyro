package io.lamart.gyro.segment

import io.lamart.gyro.*
import io.lamart.gyro.variables.OptionalVariable

interface OptionalSegment<T> : OptionalVariable<T>, Foldable<T> {

    fun <R> map(get: T.() -> R, copy: T.(R) -> T): OptionalSegment<R>

    fun filter(predicate: (T) -> Boolean): OptionalSegment<T>

    fun <R> filter(type: Class<R>): OptionalSegment<R>

    fun cast(): Segment<T>

}

fun <T> segmentOfNullable(get: () -> T?, set: (T) -> Unit) =
    optionalSegmentOf({ Foldable.maybe(get) }, set)

fun <T> optionalSegmentOf(get: () -> Foldable<T>, set: (T) -> Unit): OptionalSegment<T> =
    object : OptionalSegment<T>,
        OptionalVariable<T> by OptionalVariable(get, set),
        Foldable<T> by Foldable.wrap(get) {

        private val foldable: Foldable<T> = this

        override fun <R> map(get: T.() -> R, copy: T.(R) -> T): OptionalSegment<R> =
            optionalSegmentOf(
                { foldable.map(get) },
                { value -> get().map { copy(it, value) }.fold({}, set) }
            )

        override fun filter(predicate: (T) -> Boolean): OptionalSegment<T> =
            optionalSegmentOf({ foldable.filter(predicate) }, set)

        @Suppress("UNCHECKED_CAST")
        override fun <R> filter(type: Class<R>): OptionalSegment<R> =
            optionalSegmentOf(
                { foldable.filter(type::isInstance).map { it as R } },
                { set(it as T) }
            )

        override fun cast(): Segment<T> =
            segmentOf({ foldable.getOrElse { throw ClassCastException() } }, set)

    }

inline fun <reified R> OptionalSegment<*>.filter() = filter(R::class.java)
