package io.lamart.gyro.mutable

import io.lamart.gyro.Foldable
import io.lamart.gyro.immutable.Immutable
import io.lamart.gyro.variables.Variable
import io.lamart.gyro.variables.variableOf
import java.util.concurrent.atomic.AtomicReference

class Mutable<T>(
    private val get: () -> T,
    private val set: (T) -> Unit
) : MutableType<T>, Variable<T> by variableOf(get, set) {

    fun <R> select(transform: T.() -> R, copy: T.(R) -> T): Mutable<R> =
        Mutable(
            { get().let(transform) },
            { copy(get(), it).let(::set) }
        )

    override fun filter(predicate: T.() -> Boolean): OptionalMutable<T> =
        toOptionalMutable().filter(predicate)

    inline fun <reified R> filter() =
        filter(R::class.java::isInstance)

    @Suppress("UNCHECKED_CAST")
    fun <R> cast() =
        Mutable({ get() as R }, { set(it as T) })

    fun intercept(delegate: Mutable<T>.(value: T) -> Void): Mutable<T> =
        intercept({ it }, delegate)

    fun <R> intercept(transform: (T) -> R, delegate: Mutable<T>.(value: R) -> Void): Mutable<R> =
        Mutable({ get().let(transform) }, { delegate(this, it) })

    fun toOptionalMutable(): OptionalMutable<T> =
        OptionalMutable({ Foldable.some(get) }, set)

}

fun <T> AtomicReference<T>.toMutable() = Mutable(::get, ::set)

fun <T> Mutable<T>.toImmutable() = Immutable(this)

