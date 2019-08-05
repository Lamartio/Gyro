package io.lamart.gyro.variables

import io.lamart.gyro.Foldable
import io.lamart.gyro.Record
import io.lamart.gyro.getOrElse
import io.lamart.gyro.getOrNull
import io.lamart.gyro.mutable.mutableOfNullable
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface OptionalVariable<T> : VariableType<T>, OptionalValue<T> {

    fun update(block: T.() -> T): Unit? =
        get()?.let { before ->
            block(before)
                .takeIf { it != before }
                ?.let(::set)
        }

    fun record(block: (T) -> T): Record<T>?

}

private class OptionalVariableInstance<T>(
    private val get: () -> Foldable<T>,
    private val set: (T) -> Unit
) : OptionalVariable<T> {

    val foldable: Foldable<T>
        get() = get.invoke()

    override fun get(): T? = foldable.getOrNull()

    override fun set(value: T) = set.invoke(value)

    override fun update(block: (T) -> T): Unit? =
        foldable.getOrNull()?.let(block)?.let(set)

    override fun record(block: (T) -> T): Record<T>? =
        foldable.getOrNull()?.let { Record(it, block(it)) }

}

fun <T> variableOfNullable(value: T?): OptionalVariable<T> =
    AtomicReference(value).run { optionalVariableOf<T>({ Foldable.maybe(::get) }, ::set) }

fun <T> optionalVariableOf(get: () -> Foldable<T>, set: (T) -> Unit): OptionalVariable<T> =
    OptionalVariableInstance(get, set)

fun <T> OptionalVariable<T>.toOptionalMutable() = mutableOfNullable(::get, ::set)

fun <T> OptionalVariable<T>.toProperty(ifNone: () -> T = { throw NullPointerException() }) =
    object : ReadWriteProperty<Any?, T> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): T = this@toProperty.toFoldable().getOrElse(ifNone)

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = this@toProperty.set(value)

    }
