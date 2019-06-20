package io.lamart.gyro.observable

interface Sender<T> {
    fun subscribe(receiver: Receiver<T>): Subscription
}