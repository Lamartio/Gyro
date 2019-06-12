package io.lamart.glyph

import io.lamart.gyro.filter
import io.lamart.gyro.toSegment
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.*

class SegmentTests {

    private fun <T> gyroOf(value: T) = AtomicReference(value).toSegment()

    @Test
    fun map() {
        val (before, after) = gyroOf(Bell())
            .map({ isRinging }, { copy(isRinging = it) })
            .record { !it }

        assertFalse(before)
        assertTrue(after)
    }

    @Test
    fun typeFilter() {
        gyroOf(Door.Open as Door)
            .filter<Door.Open>()
            .get()
            .let {
                assertNotNull(it)
                assertSame(it, Door.Open)
            }

        gyroOf(Door.Open as Door)
            .filter<Door.Closed>()
            .get()
            .let { assertNull(it) }
    }

    @Test
    fun predicateFilter() {
        gyroOf(Bell())
            .filter { !it.isRinging }
            .get()
            .let {
                assertNotNull(it)
                assertSame(it.isRinging, false)
            }

        gyroOf(Bell())
            .filter { it.isRinging }
            .get()
            .let { assertNull(it) }
    }

}

