package io.lamart.gyro.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.observable.Observable
import io.lamart.gyro.observable.Subscription
import io.lamart.gyro.segment.segmentOfNullable

fun <T> MutableLiveData<T>.toSegment() = segmentOfNullable({ value }, ::setValue)

enum class LiveDataType {
    SET_VALUE,
    POST_VALUE
}

fun <T> Observable<T>.toLiveData(type: LiveDataType = LiveDataType.SET_VALUE): LiveData<T> =
    object : LiveData<T>() {

        private lateinit var subscription: Subscription
        private val observer: (T) -> Unit = when (type) {
            LiveDataType.SET_VALUE -> ::setValue
            LiveDataType.POST_VALUE -> ::postValue
        }

        override fun onActive() {
            super.onActive()
            this@toLiveData.subscribe(observer)
        }

        override fun onInactive() {
            super.onInactive()
            subscription.unsubscribe()
        }

    }