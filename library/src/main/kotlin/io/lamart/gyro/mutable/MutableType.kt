package io.lamart.gyro.mutable

import io.lamart.gyro.variables.VariableType

interface MutableType<T> : VariableType<T> {

    fun filter(predicate: T.() -> Boolean): OptionalMutable<T>

}

inline fun <reified R> MutableType<*>.filterCast() = filter(R::class.java::isInstance).cast<R>()
