package io.lamart.gyro.immutable

import io.lamart.gyro.segment.OptionalSegment
import io.lamart.gyro.segment.Segment
import io.lamart.gyro.variables.Value
import io.lamart.gyro.variables.toSegment
import io.lamart.gyro.variables.variableOf

class Immutable<T, N> private constructor(
    private val get: () -> T,
    private val next: OptionalSegment<N>
) : Copyable<T, N>, Value<T> {

    override fun get(): T = get.invoke()

    fun <R> select(transform: N.() -> R, copy: N.(R) -> N) = wrap { select(transform, copy) }

    fun filter(predicate: (N) -> Boolean) = wrap { filter(predicate) }

    inline fun <reified R> filter() = filter(R::class.java::isInstance)

    fun <R> cast(): Immutable<T, R> = wrap { cast() }

    inline fun <reified R> filterCast() = filter<R>().cast<R>()

    private fun <R> wrap(block: OptionalSegment<N>.() -> OptionalSegment<R>) =
        Immutable(get, block(next))

    override fun copy(block: (N) -> N): T {
        next.update(block)
        return get()
    }

    companion object {

        operator fun <T> invoke(segment: Segment<T>) =
            Immutable(segment::get, segment.toOptionalSegment())

    }

}

fun <T> immutableOf(value: T): Immutable<T, T> =
    variableOf(value)
        .toSegment()
        .let(Immutable.Companion::invoke)
