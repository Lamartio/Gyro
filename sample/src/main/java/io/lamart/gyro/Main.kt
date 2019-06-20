package io.lamart.gyro

import androidx.lifecycle.LiveData

interface Main {
    val data: LiveData<State>
    val actions: Actions
}