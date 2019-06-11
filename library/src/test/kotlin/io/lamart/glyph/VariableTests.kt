package io.lamart.glyph

import io.lamart.gyro.Variable
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VariableTests {

    private fun <T> variable(value: T) = AtomicReference(value).run {
        Variable(
            ::get,
            ::set
        )
    }

    @Test
    fun update() {
        val gyro = variable(false)

        gyro.update { !it }
        assertTrue(gyro.get())
    }

    @Test
    fun record() {
        val (before, after) = variable(false).record { !it }

        assertFalse(before)
        assertTrue(after)
    }

}