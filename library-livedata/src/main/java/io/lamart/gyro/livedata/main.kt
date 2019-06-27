package io.lamart.gyro.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.observable.Sender
import io.lamart.gyro.observable.Subscription
import io.lamart.gyro.segments.OptionalSegment
import io.lamart.gyro.segments.segmentOfNullable

fun <T> MutableLiveData<T>.toOptionalSegment(): OptionalSegment<T> = segmentOfNullable({ value }, ::setValue)

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
