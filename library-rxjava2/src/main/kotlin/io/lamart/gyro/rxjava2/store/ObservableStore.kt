package io.lamart.gyro.rxjava2.store

import io.lamart.gyro.mutable.Mutable
import io.lamart.gyro.rxjava2.toOptionalMutable
import io.lamart.gyro.store.Store
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject

interface ObservableStore<T, A> : Store<A> {
    val observable: Observable<T>
}

operator fun <T, A> ObservableStore<T, A>.component1() = observable
operator fun <T, A> ObservableStore<T, A>.component2() = actions

internal class ObservableStoreInstance<T, A>(
    override val observable: Observable<T>,
    override val actions: A
) : ObservableStore<T, A>

fun <T, A> BehaviorSubject<T>.toStore(
    actionsFactory: (mutable: Mutable<T>) -> A,
    ifNone: () -> T = { throw NullPointerException() }
): ObservableStore<T, A> =
    toOptionalMutable()
        .toMutable(ifNone)
        .let(actionsFactory)
        .let { ObservableStoreInstance(this, it) }

fun <T, A> ReplaySubject<T>.toStore(
    actionsFactory: (mutable: Mutable<T>) -> A,
    ifNone: () -> T = { throw NullPointerException() }
): ObservableStore<T, A> =
    toOptionalMutable()
        .toMutable(ifNone)
        .let(actionsFactory)
        .let { ObservableStoreInstance(this, it) }