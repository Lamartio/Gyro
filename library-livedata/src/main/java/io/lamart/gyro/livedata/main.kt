package io.lamart.gyro.livedata

import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.gyroOf

fun <T> MutableLiveData<T>.toGyro() = gyroOf({ value!! }, { value = it })
