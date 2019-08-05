package io.lamart.gyro.store

import io.lamart.gyro.mutable.Mutable
import io.lamart.gyro.observable.Emitter
import io.lamart.gyro.observable.Sender
import io.lamart.gyro.variables.toMutable

interface SenderStoreType<T, A> : Store<A> {
    val sender: Sender<T>
}

data class SenderStore<T, A>(
    override val sender: Sender<T>,
    override val actions: A
) : SenderStoreType<T, A>

fun <T, A> Emitter<T>.toStore(actionsFactory: (mutable: Mutable<T>) -> A) =
    toMutable()
        .let(actionsFactory)
        .let { SenderStore(this, it) }
