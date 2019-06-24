package io.lamart.gyro.segment

import io.lamart.gyro.Delegate
import io.lamart.gyro.Foldable
import io.lamart.gyro.Interceptor
import io.lamart.gyro.immutable.Immutable
import io.lamart.gyro.variables.Variable
import io.lamart.gyro.variables.variableOf
import java.util.concurrent.atomic.AtomicReference

class Segment<T>(
    private val get: () -> T,
    private val set: (T) -> Unit
) : SegmentType<T>, Variable<T> by variableOf(get, set) {

    fun <R> select(transform: T.() -> R, copy: T.(R) -> T): Segment<R> =
        Segment(
            { get().let(transform) },
            { copy(get(), it).let(::set) }
        )

    override fun filter(predicate: T.() -> Boolean): OptionalSegment<T> =
        toOptionalSegment().filter(predicate)

    inline fun <reified R> filter() =
        filter(R::class.java::isInstance)

    @Suppress("UNCHECKED_CAST")
    fun <R> cast() =
        Segment({ get() as R }, { set(it as T) })

    fun <R> intercept(interceptor: Interceptor<T, R>): Segment<R> =
        intercept(interceptor::transform, interceptor::delegate)

    fun intercept(delegate: Delegate<T,T>): Segment<T> =
        intercept({ it }, delegate)

    fun <R> intercept(transform: (T) -> R, delegate: Delegate<T,R>): Segment<R> =
        Segment({ get().let(transform) }, delegate(::set))

    fun toOptionalSegment(): OptionalSegment<T> =
        OptionalSegment({ Foldable.some(get) }, set)

}

fun <T> AtomicReference<T>.toSegment() = Segment(::get, ::set)

fun <T> Segment<T>.toImmutable() = Immutable(this)

