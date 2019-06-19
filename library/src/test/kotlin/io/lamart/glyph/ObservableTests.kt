package io.lamart.glyph

import io.lamart.gyro.observable.Observable
import io.lamart.gyro.observable.TestObserver
import io.lamart.gyro.variables.toSegment
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObservableTests {

    @Test
    fun test() {
        val observer = TestObserver<Bell>()
        val observable = Observable(Bell(false)).apply { subscribe(observer) }
        val (before, after) = observable
            .toSegment()
            .select({ isRinging }, { copy(isRinging = it) })
            .record { !it }

        assertEquals(2, observer.size)

        assertFalse(before)
        assertFalse(observer[0].isRinging)

        assertTrue(after)
        assertTrue(observer[1].isRinging)
    }

}