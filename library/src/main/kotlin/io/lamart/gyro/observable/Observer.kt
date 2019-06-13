package io.lamart.gyro.observable

typealias Observer<T> = (next: T) -> Unit

class TestObserver<T>(private val history: MutableList<T> = mutableListOf()) : Observer<T>, List<T> by history {

    override operator fun invoke(next: T) {
        history.add(next)
    }

}
