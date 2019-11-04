package io.lamart.gyro.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import io.lamart.gyro.State
import io.lamart.gyro.actions.Actions
import io.lamart.gyro.livedata.store.LiveDataStore

val Fragment.actions: Actions
    get() =
        requireContext()
            .applicationContext
            .let { it as LiveDataStore<State, Actions> }
            .actions

val Fragment.data: LiveData<State>
    get() =
        requireContext()
            .applicationContext
            .let { it as LiveDataStore<State, Actions> }
            .observable
