package io.lamart.gyro

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.actions.Actions
import io.lamart.gyro.livedata.store.LiveDataStore
import io.lamart.gyro.livedata.store.LiveDataStoreType
import io.lamart.gyro.livedata.store.toStore
import io.lamart.gyro.rxjava2.store.ObservableStore
import io.lamart.gyro.rxjava2.store.toStore
import io.lamart.gyro.store.FlowStore
import io.lamart.gyro.store.toStore
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

class MainApplication : Application(), LiveDataStoreType<State, Actions> {

    private val flowStore: FlowStore<State, Actions> =
        ConflatedBroadcastChannel(State()).toStore(::Actions)
    private val observableStore: ObservableStore<State, Actions> =
        BehaviorSubject.createDefault(State()).toStore(actionsFactory = ::Actions)
    private val liveDataStore: LiveDataStore<State, Actions> =
        MutableLiveData(State()).toStore(actionsFactory = ::Actions)

    override val data: LiveData<State> = liveDataStore.data
    override val actions: Actions = liveDataStore.actions

}
