package io.lamart.glyph

import io.lamart.gyro.variables.toVariable
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VariableTests {

    private fun <T> variable(value: T) = AtomicReference(value).toVariable()

    @Test
    fun update() {
        val variable = variable(false)

        variable.update { !it }
        assertTrue(variable.get())
    }

    @Test
    fun record() {
        val (before, after) = variable(false).record { !it }

        assertFalse(before)
        assertTrue(after)
    }

}