package io.lamart.gyro.observable

typealias Receiver<T> = (value: T) -> Unit

class TestReceiver<T>(private val history: MutableList<T> = mutableListOf()) : Receiver<T>, List<T> by history {

    override operator fun invoke(next: T) {
        history.add(next)
    }

}
