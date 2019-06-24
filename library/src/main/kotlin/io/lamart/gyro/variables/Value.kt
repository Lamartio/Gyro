package io.lamart.gyro.variables

import io.lamart.gyro.Foldable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface Value<T> {

    fun get(): T

}

fun <T> Variable<T>.toFoldable(): Foldable<T> = Foldable.some(this@toFoldable::get)

fun <T> Value<T>.toProperty(): ReadOnlyProperty<Any?, T> =
    object : ReadOnlyProperty<Any?, T> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): T = this@toProperty.get()

    }
