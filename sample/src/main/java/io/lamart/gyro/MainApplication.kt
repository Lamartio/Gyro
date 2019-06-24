package io.lamart.gyro

import android.app.Application
import androidx.lifecycle.LiveData
import io.lamart.gyro.actions.Actions
import io.lamart.gyro.livedata.toLiveData
import io.lamart.gyro.observable.Emitter
import io.lamart.gyro.utils.Component
import io.lamart.gyro.variables.toSegment

class MainApplication : Application(), Component {

    private val emitter = Emitter(State())
    override val state: LiveData<State> = emitter.toLiveData()
    override val actions: Actions = emitter.toSegment().let(::Actions)

}