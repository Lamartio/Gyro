package io.lamart.gyro.segment

import io.lamart.gyro.Foldable
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

    override fun toFoldable(): Foldable<T> = Foldable.some(get)

    fun toOptionalSegment(): OptionalSegment<T> =
        OptionalSegment({ Foldable.some(get) }, set)

}

fun <T> AtomicReference<T>.toSegment() = Segment(::get, ::set)

fun <T> Segment<T>.toImmutable() = Immutable(this)

