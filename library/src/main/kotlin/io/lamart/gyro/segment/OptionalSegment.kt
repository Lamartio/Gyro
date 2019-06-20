package io.lamart.gyro.segment

import io.lamart.gyro.Foldable
import io.lamart.gyro.filter
import io.lamart.gyro.getOrElse
import io.lamart.gyro.map
import io.lamart.gyro.variables.OptionalVariable
import io.lamart.gyro.variables.optionalVariableOf

class OptionalSegment<T>(
    private val get: () -> Foldable<T>,
    private val set: (T) -> Unit
) : SegmentType<T>, OptionalVariable<T> by optionalVariableOf(get, set) {

    private val foldable: Foldable<T>
        get() = get.invoke()

    fun <R> select(transform: T.() -> R, copy: T.(R) -> T): OptionalSegment<R> =
        OptionalSegment(
            { foldable.map(transform) },
            { value -> get.invoke().map { copy(it, value) }.fold({}, set) }
        )

    override fun filter(predicate: (T) -> Boolean): OptionalSegment<T> =
        OptionalSegment({ foldable.filter(predicate) }, set)

    inline fun <reified R> filter() = filter(R::class.java::isInstance)

    @Suppress("UNCHECKED_CAST")
    fun <R> cast(): OptionalSegment<R> =
        OptionalSegment({ foldable.map { it as R } }, { set(it as T) })

    override fun toFoldable(): Foldable<T> = foldable

    fun toSegment(): Segment<T> =
        Segment({ foldable.getOrElse { throw ClassCastException() } }, set)

}

fun <T> segmentOfNullable(get: () -> T?, set: (T) -> Unit) =
    OptionalSegment({ Foldable.maybe(get) }, set)
