package io.lamart.gyro

import java.util.concurrent.atomic.AtomicReference

fun <T> AtomicReference<T>.toGyro() = Gyro(::get, ::set)
