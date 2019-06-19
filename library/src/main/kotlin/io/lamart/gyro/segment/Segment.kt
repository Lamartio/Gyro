package io.lamart.gyro.segment

import io.lamart.gyro.Foldable
import io.lamart.gyro.filter
import io.lamart.gyro.immutable.Immutable
import io.lamart.gyro.map
import io.lamart.gyro.variables.Variable
import java.util.concurrent.atomic.AtomicReference


interface Segment<T> : SegmentType<T>, Variable<T> {

    fun <R> select(transform: T.() -> R, copy: T.(R) -> T): Segment<R>

    fun cast(): OptionalSegment<T>

}

inline fun <reified R> Segment<*>.filter() = filter(R::class.java)

fun <T> AtomicReference<T>.toSegment() = segmentOf(::get, ::set)

fun <T> Segment<T>.toImmutable() = Immutable(this)

fun <T> segmentOf(get: () -> T, set: (T) -> Unit): Segment<T> =
    SegmentInstance(get, set)

private class SegmentInstance<T>(private val get: () -> T, private val set: (T) -> Unit) : Segment<T>,
    Variable<T> by Variable(get, set),
    Foldable<T> by Foldable.some(get) {

    override fun <R> select(transform: T.() -> R, copy: T.(R) -> T): Segment<R> =
        segmentOf(
            { get().let(transform) },
            { copy(get(), it).let(::set) }
        )

    override fun filter(predicate: (T) -> Boolean): OptionalSegment<T> =
        optionalSegmentOf({ Foldable.some(get).filter(predicate) }, set)

    @Suppress("UNCHECKED_CAST")
    override fun <R> filter(type: Class<R>): OptionalSegment<R> =
        optionalSegmentOf(
            { Foldable.some(get).filter(type::isInstance).map { it as R } },
            { set(it as T) }
        )

    override fun cast(): OptionalSegment<T> =
        optionalSegmentOf({ Foldable.some(get) }, set)

}
