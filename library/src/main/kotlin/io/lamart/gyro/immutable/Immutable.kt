package io.lamart.gyro.immutable

import io.lamart.gyro.mutable.OptionalMutable
import io.lamart.gyro.mutable.Mutable
import io.lamart.gyro.variables.Value
import io.lamart.gyro.variables.toMutable
import io.lamart.gyro.variables.variableOf

/**
 * An Immutable holds the same operators as a `Mutable`, but is just meant to create a copy of the given object.
 */

class Immutable<T, N> private constructor(
    private val get: () -> T,
    private val next: OptionalMutable<N>
) : Copyable<T, N>, Value<T> {

    override fun get(): T = get.invoke()

    fun <R> select(transform: N.() -> R, copy: N.(R) -> N) = wrap { select(transform, copy) }

    fun filter(predicate: N.() -> Boolean) = wrap { filter(predicate) }

    inline fun <reified R> filter() = filter(R::class.java::isInstance)

    fun <R> cast(): Immutable<T, R> = wrap { cast<R>() }

    inline fun <reified R> filterCast() = filter<R>().cast<R>()

    private fun <R> wrap(block: OptionalMutable<N>.() -> OptionalMutable<R>) =
        Immutable(get, block(next))

    override fun copy(block: N.() -> N): T {
        next.update(block)
        return get()
    }

    companion object {

        operator fun <T> invoke(mutable: Mutable<T>) =
            Immutable(mutable::get, mutable.toOptionalMutable())

    }

}

fun <T> immutableOf(value: T): Immutable<T, T> =
    variableOf(value)
        .toMutable()
        .let(Immutable.Companion::invoke)
