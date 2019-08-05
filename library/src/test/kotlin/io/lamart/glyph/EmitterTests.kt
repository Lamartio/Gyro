package io.lamart.glyph

import io.lamart.gyro.observable.Emitter
import io.lamart.gyro.observable.TestReceiver
import io.lamart.gyro.variables.toMutable
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EmitterTests {

    @Test
    fun test() {
        val observer = TestReceiver<Bell>()
        val observable = Emitter(Bell(false)).apply { subscribe(observer) }
        val (before, after) = observable
            .toMutable()
            .select({ isRinging }, { copy(isRinging = it) })
            .record { !it }

        assertEquals(2, observer.size)

        assertFalse(before)
        assertFalse(observer[0].isRinging)

        assertTrue(after)
        assertTrue(observer[1].isRinging)
    }

}