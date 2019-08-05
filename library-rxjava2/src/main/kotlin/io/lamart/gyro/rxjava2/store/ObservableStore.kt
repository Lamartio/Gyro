package io.lamart.gyro.rxjava2.store

import io.lamart.gyro.mutable.Mutable
import io.lamart.gyro.rxjava2.toOptionalMutable
import io.lamart.gyro.store.Store
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject

interface ObservableStoreType<T, A> : Store<A> {
    val observable: Observable<T>
}

data class ObservableStore<T, A>(
    override val observable: Observable<T>,
    override val actions: A
) : ObservableStoreType<T, A>

fun <T, A> BehaviorSubject<T>.toStore(
    actionsFactory: (mutable: Mutable<T>) -> A,
    ifNone: () -> T = { throw NullPointerException() }
) =
    toOptionalMutable()
        .toMutable(ifNone)
        .let(actionsFactory)
        .let { ObservableStore(this, it) }

fun <T, A> ReplaySubject<T>.toStore(
    actionsFactory: (mutable: Mutable<T>) -> A,
    ifNone: () -> T = { throw NullPointerException() }
) =
    toOptionalMutable()
        .toMutable(ifNone)
        .let(actionsFactory)
        .let { ObservableStore(this, it) }