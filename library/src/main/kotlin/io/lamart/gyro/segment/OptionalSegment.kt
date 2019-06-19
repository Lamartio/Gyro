package io.lamart.gyro.segment

import io.lamart.gyro.Foldable
import io.lamart.gyro.filter
import io.lamart.gyro.getOrElse
import io.lamart.gyro.map
import io.lamart.gyro.variables.OptionalVariable

interface OptionalSegment<T> : SegmentType<T>, OptionalVariable<T> {

    fun <R> select(transform: T.() -> R, copy: T.(R) -> T): OptionalSegment<R>

    fun cast(): Segment<T>

}

inline fun <reified R> OptionalSegment<*>.filter() = filter(R::class.java)

fun <T> segmentOfNullable(get: () -> T?, set: (T) -> Unit) =
    optionalSegmentOf({ Foldable.maybe(get) }, set)

fun <T> optionalSegmentOf(get: () -> Foldable<T>, set: (T) -> Unit): OptionalSegment<T> =
    OptionalSegmentInstance(get, set)

private class OptionalSegmentInstance<T>(
    private val get: () -> Foldable<T>,
    private val set: (T) -> Unit
) : OptionalSegment<T>,
    OptionalVariable<T> by OptionalVariable(get, set),
    Foldable<T> by Foldable.wrap(get) {

    private val foldable: Foldable<T> = this

    override fun <R> select(transform: T.() -> R, copy: T.(R) -> T): OptionalSegment<R> =
        optionalSegmentOf(
            { foldable.map(transform) },
            { value -> get.invoke().map { copy(it, value) }.fold({}, set) }
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
