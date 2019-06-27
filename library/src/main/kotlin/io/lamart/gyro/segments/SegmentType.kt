package io.lamart.gyro.segments

import io.lamart.gyro.variables.VariableType

interface SegmentType<T> : VariableType<T> {

    fun filter(predicate: T.() -> Boolean): OptionalSegment<T>

}

inline fun <reified R> SegmentType<*>.filterCast() = filter(R::class.java::isInstance).cast<R>()
