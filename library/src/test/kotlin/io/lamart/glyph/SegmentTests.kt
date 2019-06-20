package io.lamart.glyph

import io.lamart.gyro.segment.Segment
import io.lamart.gyro.segment.filterCast
import io.lamart.gyro.segment.toSegment
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.*

class SegmentTests {

    private fun <T> segmentOf(value: T) = AtomicReference(value).toSegment()

    @Test
    fun select() {
        val (before, after) = segmentOf(Bell(false))
            .select({ isRinging }, { copy(isRinging = it) })
            .record { !it }

        assertFalse(before)
        assertTrue(after)
    }

    @Test
    fun typeFilter() {
        segmentOf(Door.Open as Door)
            .filterCast<Door.Open>()
            .get()
            .let {
                assertNotNull(it)
                assertSame(it, Door.Open)
            }

        segmentOf(Door.Open as Door)
            .filterCast<Door.Closed>()
            .get()
            .let { assertNull(it) }
    }

    @Test
    fun predicateFilter() {
        segmentOf(Bell(false))
            .filter { !isRinging }
            .get()
            .let {
                assertNotNull(it)
                assertSame(it.isRinging, false)
            }

        segmentOf(Bell(false))
            .filter { isRinging }
            .get()
            .let { assertNull(it) }
    }

}

