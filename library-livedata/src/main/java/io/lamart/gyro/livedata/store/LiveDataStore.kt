package io.lamart.gyro.livedata.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.livedata.BehaviorLiveData
import io.lamart.gyro.livedata.toOptionalSegment
import io.lamart.gyro.segments.Segment
import io.lamart.gyro.variables.Variable

data class LiveDataStore<T, A>(val sender: LiveData<T>, val actions: A)

fun <T, A> BehaviorLiveData<T>.toLiveDataStore(actionsFactory: (state: Variable<T>) -> A) =
    toLiveDataStore({ it }, actionsFactory)

fun <T, R, A> MutableLiveData<T>.toLiveDataStore(
    transform: (Segment<T>) -> Segment<R>,
    actionsFactory: (state: Variable<R>) -> A,
    ifNone: () -> T = { throw NullPointerException() }
) =
    toOptionalSegment()
        .toSegment(ifNone)
        .run(transform)
        .let(actionsFactory)
        .let { LiveDataStore(this, it) }