package io.lamart.gyro

import android.app.Application
import androidx.lifecycle.LiveData
import io.lamart.gyro.livedata.toLiveData
import io.lamart.gyro.observable.Emitter
import io.lamart.gyro.variables.toSegment

class MainApplication : Application(), Main {

    private val observable = Emitter(State())
    override val data: LiveData<State> = observable.toLiveData()
    override val actions: Actions = observable.toSegment().let(::Actions)

}