package io.lamart.gyro

import io.lamart.gyro.rxjava.toGyro
import io.reactivex.subjects.BehaviorSubject

data class User(val name: String, val age: Int)

private fun main() {
    val user = User("Danny", 28)

    BehaviorSubject
        .createDefault(user)
        .toGyro()
        .update { value ->
            value
                .toImmutable()
                .map({ name }, { copy(name = it) })
                .copy { "Olek" }
        }
}