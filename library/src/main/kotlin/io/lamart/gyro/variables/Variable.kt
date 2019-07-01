package io.lamart.gyro.variables

import io.lamart.gyro.Record
import io.lamart.gyro.segments.Segment
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface Variable<T> : VariableType<T>, Value<T> {

    fun update(block: T.() -> T) =
        get().let(block).let(::set)

    fun record(block: (T) -> T): Record<T> =
        get()
            .let { Record(it, block(it)) }
            .also { (_, after) -> set(after) }

}

private class VariableInstance<T>(private val get: () -> T, private val set: (T) -> Unit) : Variable<T> {

    override fun get(): T = get.invoke()

    override fun set(value: T) = set.invoke(value)

}

fun <T> AtomicReference<T>.toVariable(): Variable<T> = VariableInstance(::get, ::set)

fun <T> variableOf(value: T) = AtomicReference(value).toVariable()
fun <T> variableOf(get: () -> T, set: (T) -> Unit): Variable<T> = VariableInstance(get, set)

fun <T> Variable<T>.toSegment() = Segment(::get, ::set)

fun <T> Variable<T>.toProperty(): ReadWriteProperty<Any?, T> =
    object : ReadWriteProperty<Any?, T> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): T = this@toProperty.get()

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = this@toProperty.set(value)

    }
