package io.lamart.gyro.actions

import io.lamart.gyro.State
import io.lamart.gyro.segments.Segment

class Actions(val segment: Segment<State>) {

    val user: UserActions =
        segment
            .select({ user }, { copy(user = it) })
            .let(::UserActions)

    fun openDoor() {
        segment
            .select({ house }, { copy(house = it) })
            .select({ door }, { copy(door = it) })
            .filter { !isOpen }
            .select({ isOpen }, { copy(isOpen = it) })
            .set(true)
    }

    fun closeDoor() {
        segment
            .select({ house }, { copy(house = it) })
            .select({ door }, { copy(door = it) })
            .filter { isOpen }
            .select({ isOpen }, { copy(isOpen = it) })
            .set(false)
    }

    fun startRinging() =
        segment
            .select({ house }, { copy(house = it) })
            .select({ door }, { copy(door = it) })
            .select({ bell }, { copy(bell = it) })
            .filter { !isRinging }
            .select({ isRinging }, { copy(isRinging = it) })
            .set(true)


    fun stopRinging() {
        segment
            .select({ house }, { copy(house = it) })
            .select({ door }, { copy(door = it) })
            .select({ bell }, { copy(bell = it) })
            .select({ isRinging }, { copy(isRinging = it) })
            .set(false)
    }
}
