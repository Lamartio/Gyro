package io.lamart.gyro.segment

import io.lamart.gyro.Foldable
import io.lamart.gyro.variables.VariableType

interface SegmentType<T> : VariableType<T> {

    fun filter(predicate: T.() -> Boolean): OptionalSegment<T>

    fun toFoldable(): Foldable<T>

}

inline fun <reified R> SegmentType<*>.filterCast() = filter(R::class.java::isInstance).cast<R>()
