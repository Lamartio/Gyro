package io.lamart.gyro.rxjava2

import io.lamart.gyro.*
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject

fun <T> BehaviorSubject<T>.toGyro(): ConditionalGyro<T> = gyroOfNullable({ value }, ::onNext)

fun <T> ReplaySubject<T>.toGyro(): ConditionalGyro<T> = gyroOfNullable({ value }, ::onNext)
