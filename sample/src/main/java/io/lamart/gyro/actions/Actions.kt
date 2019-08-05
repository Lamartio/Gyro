package io.lamart.gyro.actions

import io.lamart.gyro.State
import io.lamart.gyro.mutable.Mutable

class Actions(val mutable: Mutable<State>) {

    val user: UserActions =
        mutable
            .select({ user }, { copy(user = it) })
            .let(::UserActions)

    fun openDoor() {
        mutable
            .select({ house }, { copy(house = it) })
            .select({ door }, { copy(door = it) })
            .filter { !isOpen }
            .select({ isOpen }, { copy(isOpen = it) })
            .set(true)
    }

    fun closeDoor() {
        mutable
            .select({ house }, { copy(house = it) })
            .select({ door }, { copy(door = it) })
            .filter { isOpen }
            .select({ isOpen }, { copy(isOpen = it) })
            .set(false)
    }

    fun startRinging() =
        mutable
            .select({ house }, { copy(house = it) })
            .select({ door }, { copy(door = it) })
            .select({ bell }, { copy(bell = it) })
            .filter { !isRinging }
            .select({ isRinging }, { copy(isRinging = it) })
            .set(true)


    fun stopRinging() {
        mutable
            .select({ house }, { copy(house = it) })
            .select({ door }, { copy(door = it) })
            .select({ bell }, { copy(bell = it) })
            .select({ isRinging }, { copy(isRinging = it) })
            .set(false)
    }
}
