package io.lamart.glyph

import io.lamart.gyro.segments.filterCast
import io.lamart.gyro.segments.toSegment
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.*


class OptionalSegmentTests {

    private fun <T> optionalSegmentOf(value: T) = AtomicReference(value).toSegment().toOptionalSegment()

    @Test
    fun select() {
        optionalSegmentOf(Bell(false))
            .select({ isRinging }, { copy(isRinging = it) })
            .record { !it }
            .let {
                assertNotNull(it)
                assertFalse(it.before)
                assertTrue(it.after)
            }

    }

    @Test
    fun typeFilterCast() {
        optionalSegmentOf(Door.Open as Door)
            .filterCast<Door.Open>()
            .get()
            .let {
                assertNotNull(it)
                assertSame(it, Door.Open)
            }

        optionalSegmentOf(Door.Open as Door)
            .filterCast<Door.Closed>()
            .get()
            .let { assertNull(it) }
    }

    @Test
    fun predicateFilter() {
        optionalSegmentOf(Bell(false))
            .filter { !isRinging }
            .get()
            .let {
                assertNotNull(it)
                assertSame(it.isRinging, false)
            }

        optionalSegmentOf(Bell(false))
            .filter { isRinging }
            .get()
            .let { assertNull(it) }
    }

}