package io.lamart.gyro.store

import io.lamart.gyro.observable.Emitter
import io.lamart.gyro.observable.Sender
import io.lamart.gyro.segments.Segment
import io.lamart.gyro.variables.Variable
import io.lamart.gyro.variables.toSegment

interface SenderStoreType<T, A> : Store<A> {
    val sender: Sender<T>
}

data class SenderStore<T, A>(
    override val sender: Sender<T>,
    override val actions: A
) : SenderStoreType<T, A>

fun <T, A> Emitter<T>.toStore(actionsFactory: (segment: Segment<T>) -> A) =
    toSegment()
        .let(actionsFactory)
        .let { SenderStore(this, it) }
