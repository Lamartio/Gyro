package io.lamart.gyro.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.observable.Observable
import io.lamart.gyro.observable.Subscription
import io.lamart.gyro.segment.segmentOfNullable

fun <T> MutableLiveData<T>.toSegment() = segmentOfNullable({ value }, ::setValue)

fun <T> Observable<T>.toLiveData(): LiveData<T> =
    object : LiveData<T>() {

        private lateinit var subscription: Subscription

        override fun onActive() {
            super.onActive()
            this@toLiveData.subscribe(::setValue)
        }

        override fun onInactive() {
            super.onInactive()
            subscription.unsubscribe()
        }

    }