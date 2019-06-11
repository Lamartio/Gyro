package io.lamart.gyro

import arrow.core.Option
import java.util.concurrent.atomic.AtomicReference

interface Gyro<T> : Variable<T>, Foldable<T> {

    fun <R> map(get: T.() -> R, copy: T.(R) -> T): Gyro<R>

    fun filter(predicate: (T) -> Boolean): ConditionalGyro<T>

    fun <R> filter(type: Class<R>): ConditionalGyro<R>

    fun cast(): ConditionalGyro<T>

}

inline fun <reified R> Gyro<*>.filter() = filter(R::class.java)

fun <T> AtomicReference<T>.toGyro() = gyroOf(::get, ::set)

fun <T> gyroOf(get: () -> T, set: (T) -> Unit): Gyro<T> =
    object : Gyro<T>,
        Variable<T> by Variable.invoke(get, set),
        Foldable<T> by Foldable.some(get) {

        override fun <R> map(get: T.() -> R, copy: T.(R) -> T): Gyro<R> =
            gyroOf(
                { get().let(get) },
                { copy(get(), it).let(::set) }
            )

        override fun filter(predicate: (T) -> Boolean): ConditionalGyro<T> =
            conditionalGyroOf({ Option.just(get()).filter(predicate) }, set)

        @Suppress("UNCHECKED_CAST")
        override fun <R> filter(type: Class<R>): ConditionalGyro<R> =
            conditionalGyroOf(
                { Option.just(get()).filter(type::isInstance).map { it as R } },
                { set(it as T) }
            )

        override fun cast(): ConditionalGyro<T> = conditionalGyroOf({ Option.just(get()) }, set)

    }
