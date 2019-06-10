package io.lamart.gyro.rxjava

import io.lamart.gyro.Gyro
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject

fun <T> BehaviorSubject<T>.toGyro(): Gyro<T> = Gyro({ value!! }, ::onNext)

fun <T> ReplaySubject<T>.toGyro(): Gyro<T> = Gyro({ value!! }, ::onNext)
