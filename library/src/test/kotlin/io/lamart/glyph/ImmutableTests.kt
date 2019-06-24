package io.lamart.glyph

import io.lamart.gyro.immutable.immutableOf
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ImmutableTests {

    @Test
    fun copy() {
        immutableOf(Bell(false))
            .copy { Bell(!isRinging) }
            .isRinging
            .let { assertTrue(it) }
    }

    @Test
    fun select() {
        immutableOf(Bell(false))
            .select({ isRinging }, { copy(isRinging = it) })
            .copy { !this }
            .isRinging
            .let { assertTrue(it) }
    }

    @Test
    fun predicateFilter() {
        immutableOf(Bell(false))
            .filter { !isRinging }
            .copy { copy(isRinging = !isRinging) }
            .isRinging
            .let { assertTrue(it) }

        immutableOf(Bell(false))
            .filter { isRinging }
            .copy { copy(isRinging = !isRinging) }
            .isRinging
            .let { assertFalse(it) }
    }

    @Test
    fun typeFilter() {
        immutableOf(Door.Open as Door)
            .filter<Door.Open>()
            .copy { Door.Closed }
            .let { assertSame(Door.Closed, it) }
        immutableOf(Door.Open as Door)
            .filter<Door.Closed>()
            .copy { Door.Closed }
            .let { assertSame(Door.Open, it) }
    }

}