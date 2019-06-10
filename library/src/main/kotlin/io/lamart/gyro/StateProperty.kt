package io.lamart.gyro

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class StateProperty<R,T>(private val gyro: Gyro<T>) : ReadWriteProperty<R, T> {

    override fun getValue(thisRef: R, property: KProperty<*>): T = gyro.value

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        gyro.value = value
    }

}