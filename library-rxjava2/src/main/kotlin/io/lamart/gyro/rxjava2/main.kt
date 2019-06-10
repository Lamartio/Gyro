package io.lamart.gyro.rxjava2

import io.lamart.gyro.Gyro
import io.lamart.gyro.gyroOf
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject

fun <T> BehaviorSubject<T>.toGyro(): Gyro<T> = gyroOf({ value!! }, ::onNext)

fun <T> ReplaySubject<T>.toGyro(): Gyro<T> = gyroOf({ value!! }, ::onNext)
