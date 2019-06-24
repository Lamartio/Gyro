package io.lamart.gyro

import android.app.Application
import io.lamart.gyro.actions.Actions
import io.lamart.gyro.observable.Emitter
import io.lamart.gyro.actions.toActions
import io.lamart.gyro.livedata.BehaviorLiveData

class MainApplication : Application(), Actions.Owner {

    private val emitter = BehaviorLiveData(State())
    override val actions: Actions = emitter.toActions()

}