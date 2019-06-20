package io.lamart.gyro.observable

typealias Receiver<T> = (next: T) -> Unit

class TestReceiver<T>(private val history: MutableList<T> = mutableListOf()) : Receiver<T>, List<T> by history {

    override operator fun invoke(next: T) {
        history.add(next)
    }

}
