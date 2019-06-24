package io.lamart.gyro.actions

import androidx.lifecycle.LiveData
import io.lamart.gyro.State
import io.lamart.gyro.livedata.BehaviorLiveData

fun BehaviorLiveData<State>.toActions(): Actions = ActionsInstance(this)

interface Actions {
    val data: LiveData<State>
    val user: UserActions

    fun openDoor()
    fun closeDoor()
    fun startRinging()
    fun stopRinging()

    interface Owner {

        val actions: Actions

    }

}

private class ActionsInstance(override val data: BehaviorLiveData<State>) : Actions {

    private val segment = data.toSegment()
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
