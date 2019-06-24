package io.lamart.gyro.variables

import io.lamart.gyro.Foldable
import io.lamart.gyro.Record
import io.lamart.gyro.getOrNull
import io.lamart.gyro.segment.segmentOfNullable
import java.util.concurrent.atomic.AtomicReference

interface OptionalVariable<T> : VariableType<T>, OptionalValue<T> {

    fun update(block: T.() -> T): Unit? =
        get()?.let { before ->
            block(before)
                .takeIf { it != before }
                ?.let(::set)
        }

    fun record(block: (T) -> T): Record<T>?

}

private class OptionalVariableInstance<T>(
    private val get: () -> Foldable<T>,
    private val set: (T) -> Unit
) : OptionalVariable<T> {

    val foldable: Foldable<T>
        get() = get.invoke()

    override fun get(): T? = foldable.getOrNull()

    override fun set(value: T) = set.invoke(value)

    override fun update(block: (T) -> T) {
        foldable.getOrNull()?.let { before ->
            val after = block(before)

            if (before != after)
                set(after)
        }
    }

    override fun record(block: (T) -> T): Record<T>? =
        foldable.fold({ null }) { before ->
            val after = block(before)

            if (before != after)
                set(after)

            Record(before, after)
        }

}

fun <T> variableOfNullable(value: T?): OptionalVariable<T> =
    AtomicReference(value).run { optionalVariableOf<T>({ Foldable.maybe(::get) }, ::set) }

fun <T> optionalVariableOf(get: () -> Foldable<T>, set: (T) -> Unit): OptionalVariable<T> =
    OptionalVariableInstance(get, set)

fun <T> OptionalVariable<T>.toOptionalSegment() = segmentOfNullable(::get, ::set)
