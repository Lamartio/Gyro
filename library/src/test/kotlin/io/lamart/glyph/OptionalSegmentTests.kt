package io.lamart.glyph

import io.lamart.gyro.segment.filter
import io.lamart.gyro.segment.toSegment
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.*


class OptionalSegmentTests {

    private fun <T> optionalSegmentOf(value: T) = AtomicReference(value).toSegment().cast()

    @Test
    fun map() {
        optionalSegmentOf(Bell(false))
            .map({ isRinging }, { copy(isRinging = it) })
            .record { !it }
            .let {
                assertNotNull(it)
                assertFalse(it.before)
                assertTrue(it.after)
            }

    }

    @Test
    fun typeFilter() {
        optionalSegmentOf(Door.Open as Door)
            .filter<Door.Open>()
            .get()
            .let {
                assertNotNull(it)
                assertSame(it, Door.Open)
            }

        optionalSegmentOf(Door.Open as Door)
            .filter<Door.Closed>()
            .get()
            .let { assertNull(it) }
    }

    @Test
    fun predicateFilter() {
        optionalSegmentOf(Bell(false))
            .filter { !it.isRinging }
            .get()
            .let {
                assertNotNull(it)
                assertSame(it.isRinging, false)
            }

        optionalSegmentOf(Bell(false))
            .filter { it.isRinging }
            .get()
            .let { assertNull(it) }
    }

}