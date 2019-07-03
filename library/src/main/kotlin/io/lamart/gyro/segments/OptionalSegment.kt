package io.lamart.gyro.segments

import io.lamart.gyro.*
import io.lamart.gyro.variables.OptionalVariable
import io.lamart.gyro.variables.optionalVariableOf
import java.lang.NullPointerException

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

    override fun filter(predicate: T.() -> Boolean): OptionalSegment<T> =
        OptionalSegment({ foldable.filter(predicate) }, set)

    inline fun <reified R> filter() = filter(R::class.java::isInstance)

    @Suppress("UNCHECKED_CAST")
    fun <R> cast(): OptionalSegment<R> =
        OptionalSegment({ foldable.map { it as R } }, { set(it as T) })

    fun intercept(delegate: OptionalSegment<T>.(value: T) -> Void): OptionalSegment<T> =
        intercept({ it }, delegate)

    fun <R> intercept(transform: (T) -> R, delegate: OptionalSegment<T>.(value: R) -> Void): OptionalSegment<R> =
        OptionalSegment({ foldable.map(transform) }, { delegate(this, it) })

    fun toSegment(ifNone: () -> T = { throw NullPointerException() }): Segment<T> =
        Segment({ foldable.getOrElse(ifNone) }, set)

}

fun <T> segmentOfNullable(get: () -> T?, set: (T) -> Unit): OptionalSegment<T> =
    OptionalSegment({ Foldable.maybe(get) }, set)
