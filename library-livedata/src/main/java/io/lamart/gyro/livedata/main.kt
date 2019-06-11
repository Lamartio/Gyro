package io.lamart.gyro.livedata

import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.Gyro
import io.lamart.gyro.gyroOf
import io.lamart.gyro.gyroOfNullable

fun <T> MutableLiveData<T>.toGyro() = gyroOfNullable({ value }, { value = it })
