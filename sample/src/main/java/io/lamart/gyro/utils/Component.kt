package io.lamart.gyro.utils

import androidx.lifecycle.LiveData
import io.lamart.gyro.State
import io.lamart.gyro.actions.Actions

interface Component {
    val state: LiveData<State>
    val actions: Actions
}