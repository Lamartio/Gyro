package io.lamart.gyro.variables

interface VariableType<T> {

    fun set(value: T)

    fun update(block: T.() -> T)

}