package io.lamart.gyro

data class State(val house: House = House(), val user: User = User.NotSignedIn())

data class House(val door: Door = Door())

data class Door(val bell: Bell = Bell(), val isOpen: Boolean = false)

data class Bell(val isRinging: Boolean = false)

sealed class User {
    data class NotSignedIn(val reason: String? = null) : User()
    object SigningIn : User()
    data class SignedIn(val token: String) : User()
}
