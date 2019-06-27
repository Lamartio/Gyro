package io.lamart.gyro.store

import io.lamart.gyro.observable.Emitter
import io.lamart.gyro.observable.Sender
import io.lamart.gyro.segments.Segment
import io.lamart.gyro.variables.Variable
import io.lamart.gyro.variables.toSegment

data class EmitterStore<T, A>(val sender: Sender<T>, val actions: A)

fun <T, A> Emitter<T>.toStore(actionsFactory: (state: Variable<T>) -> A) =
    toStore({ it }, actionsFactory)

fun <T, R, A> Emitter<T>.toStore(
    transform: (Segment<T>) -> Segment<R>,
    actionsFactory: (state: Variable<R>) -> A
) =
    toSegment()
        .run(transform)
        .let(actionsFactory)
        .let { EmitterStore(this, it) }
