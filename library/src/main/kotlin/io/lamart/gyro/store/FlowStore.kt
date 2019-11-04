package io.lamart.gyro.store

import io.lamart.gyro.mutable.Mutable
import io.lamart.gyro.mutable.mutableOf
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

interface FlowStore<T, A> : Store<A> {
    val observable: Flow<T>
}

internal class FlowStoreInstance<T, A>(
    override val observable: Flow<T>,
    override val actions: A
) : FlowStore<T, A>

fun <T, A> ConflatedBroadcastChannel<T>.toStore(actionsFactory: (mutable: Mutable<T>) -> A): FlowStore<T, A> =
    FlowStoreInstance(
        asFlow(),
        mutableOf(get = ::value, set = { offer(it) }).let(actionsFactory)
    )
