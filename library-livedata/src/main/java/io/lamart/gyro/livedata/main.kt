package io.lamart.gyro.livedata

import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.segmentOfNullable

fun <T> MutableLiveData<T>.toGyro() = segmentOfNullable({ value }, { value = it })
