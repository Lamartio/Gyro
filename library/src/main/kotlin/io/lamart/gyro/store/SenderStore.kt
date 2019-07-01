package io.lamart.gyro.store

import io.lamart.gyro.observable.Emitter
import io.lamart.gyro.observable.Sender
import io.lamart.gyro.segments.Segment
import io.lamart.gyro.variables.Variable
import io.lamart.gyro.variables.toSegment

interface SenderStore<T, A> : Store<A> {
    val sender: Sender<T>
}

data class SenderStoreInstance<T, A>(
    override val sender: Sender<T>,
    override val actions: A
) : SenderStore<T, A>

fun <T, A> Emitter<T>.toStore(
    actionsFactory: (segment: Segment<T>) -> A
): SenderStore<T, A> =
    toSegment()
        .let(actionsFactory)
        .let { SenderStoreInstance(this, it) }
