package io.lamart.gyro

import androidx.lifecycle.LiveData
import io.lamart.gyro.observable.Observable
import io.lamart.gyro.segment.Segment

interface Main {
    val data: LiveData<State>
    val actions: Actions
}