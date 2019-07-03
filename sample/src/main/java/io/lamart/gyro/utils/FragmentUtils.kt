package io.lamart.gyro.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import io.lamart.gyro.State
import io.lamart.gyro.actions.Actions
import io.lamart.gyro.livedata.store.LiveDataStore
import io.lamart.gyro.livedata.store.LiveDataStoreType

val Fragment.actions: Actions
    get() =
        requireContext()
            .applicationContext
            .let { it as LiveDataStoreType<State, Actions> }
            .actions

val Fragment.data: LiveData<State>
    get() =
        requireContext()
            .applicationContext
            .let { it as LiveDataStoreType<State, Actions> }
            .data
