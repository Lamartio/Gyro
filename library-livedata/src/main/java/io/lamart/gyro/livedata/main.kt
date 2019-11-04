package io.lamart.gyro.livedata

import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.mutable.OptionalMutable
import io.lamart.gyro.mutable.mutableOfNullable

fun <T> MutableLiveData<T>.toOptionalMutable(): OptionalMutable<T> = mutableOfNullable({ value }, ::setValue)
