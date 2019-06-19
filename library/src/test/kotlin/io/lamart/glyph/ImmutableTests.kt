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
            .copy { Bell(!it.isRinging) }
            .isRinging
            .let { assertTrue(it) }
    }

    @Test
    fun select() {
        immutableOf(Bell(false))
            .select({ isRinging }, { copy(isRinging = it) })
            .copy { !it }
            .isRinging
            .let { assertTrue(it) }
    }

    @Test
    fun predicateFilter() {
        immutableOf(Bell(false))
            .filter { !it.isRinging }
            .copy { it.copy(isRinging = !it.isRinging) }
            .isRinging
            .let { assertTrue(it) }

        immutableOf(Bell(false))
            .filter { it.isRinging }
            .copy { it.copy(isRinging = !it.isRinging) }
            .isRinging
            .let { assertFalse(it) }
    }

    @Test
    fun typeFilter() {
        immutableOf(Door.Open as Door)
            .filter { it is Door.Open }
            .copy { Door.Closed }
            .let { assertSame(Door.Closed, it) }
        immutableOf(Door.Open as Door)
            .filter { it is Door.Closed }
            .copy { Door.Closed }
            .let { assertSame(Door.Open, it) }
    }

}