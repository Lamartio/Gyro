package io.lamart.gyro.segments

interface Interceptor<T, R> {

    fun transform(value: T): R

    fun delegate(set: (T) -> Unit): (value: R) -> Unit

}

typealias Delegate<T, R> = (set: (T) -> Unit) -> (value: R) -> Unit