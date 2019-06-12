package io.lamart.gyro.rxjava2

import io.lamart.gyro.observable.Observable
import io.lamart.gyro.segment.OptionalSegment
import io.lamart.gyro.segment.segmentOfNullable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.Subject

fun <T> BehaviorSubject<T>.toSegment(): OptionalSegment<T> =
    segmentOfNullable({ value }, ::onNext)

fun <T> ReplaySubject<T>.toSegment(): OptionalSegment<T> =
    segmentOfNullable({ value }, ::onNext)
