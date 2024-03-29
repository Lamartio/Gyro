package io.lamart.gyro.variables

import io.lamart.gyro.Foldable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface OptionalValue<T> {

    fun get(): T?

}

fun <T> OptionalValue<T>.toFoldable(): Foldable<T> = Foldable.maybe(this@toFoldable::get)

fun <T> OptionalValue<T>.toProperty(): ReadOnlyProperty<Any?, T?> =
    object : ReadOnlyProperty<Any?, T?> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): T? = this@toProperty.get()

    }
