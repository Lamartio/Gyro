package io.lamart.gyro.rxjava2.store

import io.lamart.gyro.rxjava2.toOptionalSegment
import io.lamart.gyro.segments.Segment
import io.lamart.gyro.variables.Variable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

data class ObservableStore<T, A>(val sender: Observable<T>, val actions: A)

fun <T, A> BehaviorSubject<T>.toStore(actionsFactory: (state: Variable<T>) -> A) =
    toStore({ it }, actionsFactory)

fun <T, R, A> BehaviorSubject<T>.toStore(
    transform: (Segment<T>) -> Segment<R>,
    actionsFactory: (state: Variable<R>) -> A,
    ifNone: () -> T = { throw NullPointerException() }
) =
    toOptionalSegment()
        .toSegment(ifNone)
        .run(transform)
        .let(actionsFactory)
        .let { ObservableStore(this, it) }