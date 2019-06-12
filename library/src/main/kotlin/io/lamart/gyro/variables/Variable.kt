package io.lamart.gyro.variables

import io.lamart.gyro.Record
import io.lamart.gyro.segment.Segment
import io.lamart.gyro.segment.segmentOf
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface Variable<T> : Value<T> {

    fun set(value: T)

    fun update(block: (T) -> T) = get().let(block).let(::set)

    fun record(block: (T) -> T): Record<T> =
        get().let { before ->
            block(before)
                .also(::set)
                .let { Record(before, it) }
        }

    companion object {

        internal operator fun <T> invoke(get: () -> T, set: (T) -> Unit) =
            object : Variable<T> {

                override fun get(): T = get.invoke()

                override fun set(value: T) = set.invoke(value)

            }

    }

}

fun <T> variableOf(value: T) = AtomicReference(value).toVariable()

fun <T> AtomicReference<T>.toVariable() = Variable(::get, ::set)

fun <T> Variable<T>.toSegment() = segmentOf(::get, ::set)

fun <T> Variable<T>.toProperty(): ReadWriteProperty<Any?, T> =
    object : ReadWriteProperty<Any?, T> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): T = this@toProperty.get()

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = this@toProperty.set(value)

    }
