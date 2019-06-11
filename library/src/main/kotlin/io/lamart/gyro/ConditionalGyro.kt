package io.lamart.gyro

import arrow.core.Option
import arrow.core.getOrElse

interface ConditionalGyro<T> : OptionalVariable<T>, Foldable<T> {

    fun <R> map(get: T.() -> R, copy: T.(R) -> T): ConditionalGyro<R>

    fun filter(predicate: (T) -> Boolean): ConditionalGyro<T>

    fun <R> filter(type: Class<R>): ConditionalGyro<R>

    fun cast(): Gyro<T>

}

fun <T> gyroOfNullable(get: () -> T?, set: (T) -> Unit) =
    conditionalGyroOf({ Option.fromNullable(get()) }, set)

fun <T> conditionalGyroOf(get: () -> Option<T>, set: (T) -> Unit): ConditionalGyro<T> =
    object : ConditionalGyro<T>,
        OptionalVariable<T> by OptionalVariable.invoke(get, set),
        Foldable<T> by Foldable.maybe(get) {

        private val option: Option<T>
            get() = get()

        override fun <R> map(get: T.() -> R, copy: T.(R) -> T): ConditionalGyro<R> =
            conditionalGyroOf(
                { get().map(get) },
                { value -> get().map { copy(it, value) }.map(set) }
            )

        override fun filter(predicate: (T) -> Boolean): ConditionalGyro<T> =
            conditionalGyroOf({ option.filter(predicate) }, set)

        @Suppress("UNCHECKED_CAST")
        override fun <R> filter(type: Class<R>): ConditionalGyro<R> =
            conditionalGyroOf(
                { option.filter(type::isInstance).map { it as R } },
                { set(it as T) }
            )

        override fun cast(): Gyro<T> = gyroOf({ option.getOrElse { throw ClassCastException() } }, set)

    }

inline fun <reified R> ConditionalGyro<*>.filter() = filter(R::class.java)
