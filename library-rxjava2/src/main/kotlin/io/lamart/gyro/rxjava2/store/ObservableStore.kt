package io.lamart.gyro.rxjava2.store

import io.lamart.gyro.rxjava2.toOptionalSegment
import io.lamart.gyro.segments.Segment
import io.lamart.gyro.store.Store
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject


interface ObservableStore<T, A> : Store<A> {
    val sender: Observable<T>
}

private data class ObservableStoreInstance<T, A>(
    override val sender: Observable<T>,
    override val actions: A
) : ObservableStore<T, A>

fun <T, A> BehaviorSubject<T>.toStore(
    actionsFactory: (segment: Segment<T>) -> A,
    ifNone: () -> T = { throw NullPointerException() }
): ObservableStore<T, A> =
    toOptionalSegment()
        .toSegment(ifNone)
        .let(actionsFactory)
        .let { ObservableStoreInstance(this, it) }

fun <T, A> ReplaySubject<T>.toStore(
    actionsFactory: (segment: Segment<T>) -> A,
    ifNone: () -> T = { throw NullPointerException() }
): ObservableStore<T, A> =
    toOptionalSegment()
        .toSegment(ifNone)
        .let(actionsFactory)
        .let { ObservableStoreInstance(this, it) }