package io.lamart.gyro

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.actions.Actions
import io.lamart.gyro.livedata.BehaviorLiveData
import io.lamart.gyro.livedata.store.LiveDataStore
import io.lamart.gyro.livedata.store.toLiveDataStore

class MainApplication : Application(), LiveDataStore<State, Actions> {

    private val store: LiveDataStore<State, Actions> = MutableLiveData(State()).toLiveDataStore(actionsFactory = { Actions(it) })

    override val data: LiveData<State> = store.data
    override val actions: Actions = store.actions

}