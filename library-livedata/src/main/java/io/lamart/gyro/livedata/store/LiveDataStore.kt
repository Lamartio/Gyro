package io.lamart.gyro.livedata.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.livedata.BehaviorLiveData
import io.lamart.gyro.livedata.toOptionalSegment
import io.lamart.gyro.segments.Segment
import io.lamart.gyro.store.Store
import io.lamart.gyro.variables.Variable

interface LiveDataStoreType<T, A> : Store<A> {
    val data: LiveData<T>
}

data class LiveDataStore<T, A>(
    override val data: LiveData<T>,
    override val actions: A
): LiveDataStoreType<T, A>

fun <T, A> BehaviorLiveData<T>.toLiveDataStore(actionsFactory: (segment: Segment<T>) -> A) =
    toSegment()
        .let(actionsFactory)
        .let { LiveDataStore(this, it) }

fun <T, A> MutableLiveData<T>.toLiveDataStore(
    actionsFactory: (segment: Segment<T>) -> A,
    ifNone: () -> T = { throw NullPointerException() }
) =
    toOptionalSegment()
        .toSegment(ifNone)
        .let(actionsFactory)
        .let { LiveDataStore(this, it) }