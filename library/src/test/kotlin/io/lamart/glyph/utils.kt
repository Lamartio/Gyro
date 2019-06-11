package io.lamart.glyph


data class Bell(val isRinging: Boolean = false)

sealed class Door(val bell: Bell = Bell()) {

    object Open : Door()
    object Closed : Door()

}