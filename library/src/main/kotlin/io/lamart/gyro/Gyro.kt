package io.lamart.gyro

import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadWriteProperty

class Gyro<T>(
    private val get: () -> T,
    private val set: (T) -> Unit
) {

    var value: T
        get() = get()
        set(value) = set(value)

    fun <R> map(get: T.() -> R, set: T.(R) -> Unit) = Gyro({ value.run(get) }, { set(value, it) })

    fun filter(predicate: (T) -> Boolean) = Gyro(::value) { it.takeIf(predicate)?.let { value = it } }

    @Suppress("UNCHECKED_CAST")
    fun <R> cast() = Gyro({ value as R }, { value = it as T })

    inline fun <reified R> castIf() = filter { it is R }.cast<R>()

    fun update(block: (T) -> T) = value.let(block).let { value = it }

    fun record(block: (T) -> T): Record<T> {
        val before: T = value
        val after: T = block(before)
        val record = Record(before, after)

        value = after
        return record
    }

    fun toProperty(): ReadWriteProperty<Any?, T> = StateProperty(this)

    companion object {

        operator fun <T> invoke(value: T) = AtomicReference(value).run { Gyro(::get, ::set) }

    }

}

data class Record<T>(val before: T, val after: T)