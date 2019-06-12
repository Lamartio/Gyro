package io.lamart.gyro.variables

import io.lamart.gyro.Foldable
import io.lamart.gyro.Record
import io.lamart.gyro.getOrNull
import io.lamart.gyro.segment.segmentOfNullable
import java.util.concurrent.atomic.AtomicReference

interface OptionalVariable<T> : OptionalValue<T> {

    fun set(value: T)

    fun update(block: (T) -> T)

    fun record(block: (T) -> T): Record<T>?

    companion object {

        internal operator fun <T> invoke(get: () -> Foldable<T>, set: (T) -> Unit) =
            object : OptionalVariable<T> {

                val foldable: Foldable<T>
                    get() = get()

                override fun get(): T? = foldable.getOrNull()

                override fun set(value: T) = set.invoke(value)

                override fun update(block: (T) -> T) {
                    foldable.getOrNull()?.let(block)?.let(set)
                }

                override fun record(block: (T) -> T): Record<T>? =
                    foldable.fold(
                        { null },
                        { before -> block(before).also(set).let { Record(before, it) } }
                    )

            }

    }

}

fun <T> variableOfNullable(value: T?) = AtomicReference(value).toOptionalVariable()

fun <T> AtomicReference<T?>.toOptionalVariable() = OptionalVariable({ Foldable.maybe(::get) }, ::set)

fun <T> OptionalVariable<T>.toSegment() = segmentOfNullable(::get, ::set)
