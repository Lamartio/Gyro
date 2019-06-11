package io.lamart.gyro

import arrow.core.Option

interface OptionalVariable<T> : OptionalValue<T> {

    fun set(value: T)

    fun update(block: (T) -> T)

    fun record(block: (T) -> T): Record<T>?

    companion object {

        internal operator fun <T> invoke(get: () -> Option<T>, set: (T) -> Unit) =
            object : OptionalVariable<T> {

                private val option: Option<T>
                    get() = get()

                override fun get(): T? = option.orNull()

                override fun set(value: T) = set.invoke(value)

                override fun update(block: (T) -> T) {
                    option.map(block).map(set)
                }

                override fun record(block: (T) -> T): Record<T>? =
                    option
                        .map { before ->
                            block(before)
                                .also(set)
                                .let { Record(before, it) }
                        }
                        .orNull()

            }

    }

}
