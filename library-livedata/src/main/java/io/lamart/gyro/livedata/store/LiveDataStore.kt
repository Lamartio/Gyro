package io.lamart.gyro.livedata.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.livedata.BehaviorLiveData
import io.lamart.gyro.livedata.toOptionalMutable
import io.lamart.gyro.mutable.Mutable
import io.lamart.gyro.store.Store

interface LiveDataStoreType<T, A> : Store<A> {
    val data: LiveData<T>
}

data class LiveDataStore<T, A>(
    override val data: LiveData<T>,
    override val actions: A
): LiveDataStoreType<T, A>

fun <T, A> BehaviorLiveData<T>.toLiveDataStore(actionsFactory: (mutable: Mutable<T>) -> A) =
    toMutable()
        .let(actionsFactory)
        .let { LiveDataStore(this, it) }

fun <T, A> MutableLiveData<T>.toLiveDataStore(
    actionsFactory: (mutable: Mutable<T>) -> A,
    ifNone: () -> T = { throw NullPointerException() }
) =
    toOptionalMutable()
        .toMutable(ifNone)
        .let(actionsFactory)
        .let { LiveDataStore(this, it) }