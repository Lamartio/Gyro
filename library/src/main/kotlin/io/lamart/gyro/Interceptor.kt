package io.lamart.gyro

interface Interceptor<T, R> {

    fun transform(value: T): R

    fun delegate(set: (T) -> Unit): (value: R) -> Unit

}
