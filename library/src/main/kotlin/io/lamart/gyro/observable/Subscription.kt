package io.lamart.gyro.observable

interface Subscription {

    val isSubscribed: Boolean

    fun unsubscribe()

}