package io.lamart.gyro

import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadWriteProperty

fun <T> T.toImmutable(): Immutable<T, T> = AtomicReference(this).toGyro().let { Immutable(it, it) }

class Immutable<T, R> internal constructor(
    private val origin: Gyro<T>,
    private val current: Gyro<R>
) {

    var value: R
        get() = current.value
        set(value) {
            current.value = value
        }

    fun <N> map(get: R.() -> N, set: R.(N) -> Unit) = wrap { map(get, set) }

    fun filter(predicate: (R) -> Boolean) = wrap { filter(predicate) }

    fun <N> cast() = wrap { cast<N>() }

    inline fun <reified N> castIf() = filter { it is N }.cast<N>()

    fun copy(block: (R) -> R): T {
        current.update(block)
        return origin.value
    }

    fun toProperty(): ReadWriteProperty<Any?, R> = StateProperty(current)

    private fun <N> wrap(transform: Gyro<R>.() -> Gyro<N>) = Immutable(origin, transform(current))


}