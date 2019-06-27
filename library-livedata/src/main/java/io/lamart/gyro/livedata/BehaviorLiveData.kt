package io.lamart.gyro.livedata

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.lamart.gyro.segments.Segment

/**
 * A `MutableLiveData` that is ensured to give a value.
 */

class BehaviorLiveData<T>(default: T) : MutableLiveData<T>() {

    init {
        value = default
    }

    @NonNull
    override fun getValue(): T = super.getValue()!!

    fun toSegment() = Segment(::getValue, ::setValue)

}
