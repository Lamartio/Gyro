package io.lamart.gyro.mutable

import io.lamart.gyro.*
import io.lamart.gyro.variables.OptionalVariable
import io.lamart.gyro.variables.optionalVariableOf
import java.lang.NullPointerException

class OptionalMutable<T>(
    private val get: () -> Foldable<T>,
    private val set: (T) -> Unit
) : MutableType<T>, OptionalVariable<T> by optionalVariableOf(get, set) {

    private val foldable: Foldable<T>
        get() = get.invoke()

    fun <R> select(transform: T.() -> R, copy: T.(R) -> T): OptionalMutable<R> =
        OptionalMutable(
            { foldable.map(transform) },
            { value -> get.invoke().map { copy(it, value) }.fold({}, set) }
        )

    override fun filter(predicate: T.() -> Boolean): OptionalMutable<T> =
        OptionalMutable({ foldable.filter(predicate) }, set)

    inline fun <reified R> filter() = filter(R::class.java::isInstance)

    @Suppress("UNCHECKED_CAST")
    fun <R> cast(): OptionalMutable<R> =
        OptionalMutable({ foldable.map { it as R } }, { set(it as T) })

    fun intercept(delegate: OptionalMutable<T>.(value: T) -> Void): OptionalMutable<T> =
        intercept({ it }, delegate)

    fun <R> intercept(transform: (T) -> R, delegate: OptionalMutable<T>.(value: R) -> Void): OptionalMutable<R> =
        OptionalMutable({ foldable.map(transform) }, { delegate(this, it) })

    fun toMutable(ifNone: () -> T = { throw NullPointerException() }): Mutable<T> =
        Mutable({ foldable.getOrElse(ifNone) }, set)

}

fun <T> mutableOfNullable(get: () -> T?, set: (T) -> Unit): OptionalMutable<T> =
    OptionalMutable({ Foldable.maybe(get) }, set)
