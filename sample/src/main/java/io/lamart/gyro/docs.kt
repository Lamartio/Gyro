package io.lamart.gyro

import io.lamart.gyro.immutable.Immutable
import io.lamart.gyro.immutable.immutableOf


data class House(val door: Door = Door())

data class Door(val isOpen: Boolean = false, val bell: Bell = Bell())

data class Bell(val isRinging: Boolean = false)

fun ringBell(house: House = House()): House =
    if (!house.door.isOpen && house.door.bell.isRinging) {
        house.copy(door = house.door.copy(bell = house.door.bell.copy(isRinging = true)))
    } else {
        house
    }

fun ringBell2(house: House = House()): House {
    val bell: Immutable<House, Boolean> = immutableOf(house)
        .select({ door }, { copy(door = it) })
        .filter { door -> !door.isOpen }
        .select({ bell }, { copy(bell = it) })
        .filter { bell -> !bell.isRinging }
        .select({ isRinging }, { copy(isRinging = it) })

    return bell.copy { isRinging -> !isRinging }
}
