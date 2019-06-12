package io.lamart.gyro.immutable

interface Copyable<T, N> {

    fun copy(block: (N) -> N): T

}