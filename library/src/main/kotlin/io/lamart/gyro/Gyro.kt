package io.lamart.gyro

import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadWriteProperty

fun <T> gyroOf(get: () -> T, set: (T) -> Unit): Gyro<T> =
    object : Gyro<T> {

        override var value: T
            get() = get()
            set(value) = set(value)

    }

fun <T> AtomicReference<T>.toGyro() = gyroOf(::get, ::set)

interface Gyro<T> {

    var value: T

    fun <R> map(get: T.() -> R, set: T.(R) -> Unit) = gyroOf({ value.run(get) }, { set(value, it) })

    fun filter(predicate: (T) -> Boolean) = gyroOf(::value) { it.takeIf(predicate)?.let { value = it } }

    @Suppress("UNCHECKED_CAST")
    fun <R> cast() = gyroOf({ value as R }, { value = it as T })

    fun update(block: (T) -> T) = value.let(block).let { value = it }

    fun record(block: (T) -> T): Record<T> {
        val before: T = value
        val after: T = block(before)
        val record = Record(before, after)

        value = after
        return record
    }

    fun toProperty(): ReadWriteProperty<Any?, T> = StateProperty(this)

}

inline fun <reified R> Gyro<*>.castIf() = filter { it is R }.cast<R>()
