package io.lamart.glyph

import io.lamart.gyro.mutable.filterCast
import io.lamart.gyro.mutable.toMutable
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.*


class OptionalMutableTests {

    private fun <T> optionalMutableOf(value: T) = AtomicReference(value).toMutable().toOptionalMutable()

    @Test
    fun select() {
        optionalMutableOf(Bell(false))
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
        optionalMutableOf(Door.Open as Door)
            .filterCast<Door.Open>()
            .get()
            .let {
                assertNotNull(it)
                assertSame(it, Door.Open)
            }

        optionalMutableOf(Door.Open as Door)
            .filterCast<Door.Closed>()
            .get()
            .let { assertNull(it) }
    }

    @Test
    fun predicateFilter() {
        optionalMutableOf(Bell(false))
            .filter { !isRinging }
            .get()
            .let {
                assertNotNull(it)
                assertSame(it.isRinging, false)
            }

        optionalMutableOf(Bell(false))
            .filter { isRinging }
            .get()
            .let { assertNull(it) }
    }

}