package io.lamart.gyro.actions

import io.lamart.gyro.State
import io.lamart.gyro.segments.Segment

interface Actions {
    val user: UserActions

    fun openDoor()
    fun closeDoor()
    fun startRinging()
    fun stopRinging()

    companion object {

        operator fun invoke(segment: Segment<State>): Actions = ActionsInstance(segment)

    }

}

private class ActionsInstance(val segment: Segment<State>) : Actions {

    override val user: UserActions =
        segment
            .select({ user }, { copy(user = it) })
            .let(::UserActions)

    override fun openDoor() {
        segment
            .select({ house }, { copy(house = it) })
            .select({ door }, { copy(door = it) })
            .filter { !isOpen }
            .select({ isOpen }, { copy(isOpen = it) })
            .set(true)
    }

    override fun closeDoor() {
        segment
            .select({ house }, { copy(house = it) })
            .select({ door }, { copy(door = it) })
            .filter { isOpen }
            .select({ isOpen }, { copy(isOpen = it) })
            .set(false)
    }

    override fun startRinging() =
        segment
            .select({ house }, { copy(house = it) })
            .select({ door }, { copy(door = it) })
            .select({ bell }, { copy(bell = it) })
            .filter { !isRinging }
            .select({ isRinging }, { copy(isRinging = it) })
            .set(true)


    override fun stopRinging() {
        segment
            .select({ house }, { copy(house = it) })
            .select({ door }, { copy(door = it) })
            .select({ bell }, { copy(bell = it) })
            .select({ isRinging }, { copy(isRinging = it) })
            .set(false)
    }
}
