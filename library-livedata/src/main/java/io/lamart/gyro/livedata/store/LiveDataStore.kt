package io.lamart.gyro.livedata.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.livedata.BehaviorLiveData
import io.lamart.gyro.livedata.toOptionalSegment
import io.lamart.gyro.segments.Segment
import io.lamart.gyro.store.Store
import io.lamart.gyro.variables.Variable

interface LiveDataStore<T, A> : Store<A> {
    val data: LiveData<T>
    override val actions: A
}

private class LiveDataStoreInstance<T, A>(
    override val data: LiveData<T>,
    override val actions: A
) : LiveDataStore<T, A>

fun <T, A> BehaviorLiveData<T>.toLiveDataStore(
    actionsFactory: (segment: Segment<T>) -> A
): LiveDataStore<T, A> =
    toSegment()
        .let(actionsFactory)
        .let { LiveDataStoreInstance(this, it) }

fun <T, A> MutableLiveData<T>.toLiveDataStore(
    actionsFactory: (segment: Segment<T>) -> A,
    ifNone: () -> T = { throw NullPointerException() }
): LiveDataStore<T, A> =
    toOptionalSegment()
        .toSegment(ifNone)
        .let(actionsFactory)
        .let { LiveDataStoreInstance(this, it) }