package io.lamart.gyro.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.mutable.OptionalMutable
import io.lamart.gyro.mutable.mutableOfNullable
import io.lamart.gyro.observable.Sender
import io.lamart.gyro.observable.Subscription

fun <T> MutableLiveData<T>.toOptionalMutable(): OptionalMutable<T> = mutableOfNullable({ value }, ::setValue)

fun <T> Sender<T>.toLiveData(onNext: MutableLiveData<T>.(value: T) -> Unit = { setValue(it) }): LiveData<T> =
    object : MutableLiveData<T>() {

        private lateinit var subscription: Subscription

        override fun onActive() {
            super.onActive()
            subscription = this@toLiveData.subscribe { onNext(it) }
        }

        override fun onInactive() {
            super.onInactive()
            subscription.unsubscribe()
        }

    }
