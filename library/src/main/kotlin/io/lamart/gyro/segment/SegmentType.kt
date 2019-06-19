package io.lamart.gyro.segment

import io.lamart.gyro.Foldable

interface SegmentType<T> : Foldable<T> {

    fun filter(predicate: (T) -> Boolean): OptionalSegment<T>

    fun <R> filter(type: Class<R>): OptionalSegment<R>

}