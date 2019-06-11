package io.lamart.gyro

interface ConditionalGyro<T> : OptionalVariable<T>, Foldable<T> {

    fun <R> map(get: T.() -> R, copy: T.(R) -> T): ConditionalGyro<R>

    fun filter(predicate: (T) -> Boolean): ConditionalGyro<T>

    fun <R> filter(type: Class<R>): ConditionalGyro<R>

    fun cast(): Gyro<T>

}

fun <T> gyroOfNullable(get: () -> T?, set: (T) -> Unit) =
    conditionalGyroOf({ Foldable.maybe(get) }, set)

fun <T> conditionalGyroOf(get: () -> Foldable<T>, set: (T) -> Unit): ConditionalGyro<T> =
    object : ConditionalGyro<T>,
        OptionalVariable<T> by OptionalVariable(get, set),
        Foldable<T> by Foldable.wrap(get) {

        private val foldable: Foldable<T> = this

        override fun <R> map(get: T.() -> R, copy: T.(R) -> T): ConditionalGyro<R> =
            conditionalGyroOf(
                { foldable.map(get) },
                { value -> get().map { copy(it, value) }.map(set) }
            )

        override fun filter(predicate: (T) -> Boolean): ConditionalGyro<T> =
            conditionalGyroOf({ foldable.filter(predicate) }, set)

        @Suppress("UNCHECKED_CAST")
        override fun <R> filter(type: Class<R>): ConditionalGyro<R> =
            conditionalGyroOf(
                { foldable.filter(type::isInstance).map { it as R } },
                { set(it as T) }
            )

        override fun cast(): Gyro<T> = gyroOf({ foldable.getOrElse { throw ClassCastException() } }, set)

    }

inline fun <reified R> ConditionalGyro<*>.filter() = filter(R::class.java)
