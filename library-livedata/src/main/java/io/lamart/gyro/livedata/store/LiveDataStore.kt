package io.lamart.gyro.livedata.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.livedata.BehaviorLiveData
import io.lamart.gyro.livedata.toOptionalMutable
import io.lamart.gyro.mutable.Mutable
import io.lamart.gyro.store.Store

interface LiveDataStore<T, A> : Store<A> {
    val observable: LiveData<T>
}

operator fun <T, A> LiveDataStore<T, A>.component1() = observable
operator fun <T, A> LiveDataStore<T, A>.component2() = actions

internal class LiveDataStoreInstance<T, A>(
    override val observable: LiveData<T>,
    override val actions: A
) : LiveDataStore<T, A>

fun <T, A> BehaviorLiveData<T>.toStore(actionsFactory: (mutable: Mutable<T>) -> A): LiveDataStore<T, A> =
    toMutable()
        .let(actionsFactory)
        .let { LiveDataStoreInstance(this, it) }

fun <T, A> MutableLiveData<T>.toStore(
    actionsFactory: (mutable: Mutable<T>) -> A,
    ifNone: () -> T = { throw NullPointerException() }
): LiveDataStore<T, A> =
    toOptionalMutable()
        .toMutable(ifNone)
        .let(actionsFactory)
        .let { LiveDataStoreInstance(this, it) }