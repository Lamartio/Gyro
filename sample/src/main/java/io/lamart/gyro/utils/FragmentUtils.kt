package io.lamart.gyro.utils

import androidx.fragment.app.Fragment
import io.lamart.gyro.actions.Actions

val Fragment.actions: Actions
    get() =
        requireContext()
            .applicationContext
            .let { it as Actions.Owner }
            .actions
