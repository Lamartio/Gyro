package io.lamart.glyph

import io.lamart.gyro.variables.OptionalVariable
import io.lamart.gyro.variables.variableOfNullable
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OptionalVariableTests {

    private fun <T> optionalVariableOf(value: T): OptionalVariable<T> =
        variableOfNullable(value)

    @Test
    fun update() {
        val variable = optionalVariableOf(false)

        variable.update { !this }
        variable.get().let {
            assertNotNull(it)
            assertTrue(it)
        }
    }

    @Test
    fun record() {
        optionalVariableOf(false)
            .record { !it }
            .let {
                assertNotNull(it)
                assertFalse(it.before)
                assertTrue(it.after)
            }
    }

}