package io.lamart.gyro.immutable

import io.lamart.gyro.segment.OptionalSegment
import io.lamart.gyro.segment.Segment
import io.lamart.gyro.variables.toSegment
import io.lamart.gyro.variables.variableOf

fun <T> immutableOf(value: T): Immutable<T, T> =
    variableOf(value)
        .toSegment()
        .let { Immutable(it) }

interface Immutable<T, N> : Copyable<T, N> {

    fun <R> map(get: N.() -> R, set: N.(R) -> N): Immutable<T, R>

    fun filter(predicate: (N) -> Boolean): Immutable<T, N>

    fun <R> filter(type: Class<R>): Immutable<T, R>

    companion object {

        internal operator fun <T> invoke(segment: Segment<T>): Immutable<T, T> =
            ImmutableInstance(segment, segment.cast())

    }

}

inline fun <T, reified R> Immutable<T, *>.filter() = filter(R::class.java)

private class ImmutableInstance<T, N>(
    private val origin: Segment<T>,
    private val next: OptionalSegment<N>
) : Immutable<T, N> {

    override fun <R> map(get: N.() -> R, set: N.(R) -> N) = wrap { next.map(get, set) }

    override fun filter(predicate: (N) -> Boolean) = wrap { next.filter(predicate) }

    override fun <R> filter(type: Class<R>) = wrap { next.filter(type) }

    fun <R> wrap(block: OptionalSegment<N>.() -> OptionalSegment<R>) =
        ImmutableInstance(origin, block(next))

    override fun copy(block: (N) -> N): T {
        next.update(block)
        return origin.get()
    }

}
