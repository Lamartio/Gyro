package io.lamart.glyph

import io.lamart.gyro.mutable.filterCast
import io.lamart.gyro.mutable.toMutable
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.*

class MutableTests {

    private fun <T> mutableOf(value: T) = AtomicReference(value).toMutable()

    @Test
    fun select() {
        val (before, after) = mutableOf(Bell(false))
            .select({ isRinging }, { copy(isRinging = it) })
            .record { !it }

        assertFalse(before)
        assertTrue(after)
    }

    @Test
    fun typeFilter() {
        mutableOf(Door.Open as Door)
            .filterCast<Door.Open>()
            .get()
            .let {
                assertNotNull(it)
                assertSame(it, Door.Open)
            }

        mutableOf(Door.Open as Door)
            .filterCast<Door.Closed>()
            .get()
            .let { assertNull(it) }
    }

    @Test
    fun predicateFilter() {
        mutableOf(Bell(false))
            .filter { !isRinging }
            .get()
            .let {
                assertNotNull(it)
                assertSame(it.isRinging, false)
            }

        mutableOf(Bell(false))
            .filter { isRinging }
            .get()
            .let { assertNull(it) }
    }

}

