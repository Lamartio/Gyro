package io.lamart.glyph

import io.lamart.gyro.Foldable
import io.lamart.gyro.variables.OptionalVariable
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OptionalVariableTests {

    private fun <T> optionalVariableOf(value: T) =
        AtomicReference(value).run { OptionalVariable({ Foldable.some<T>(::get) }, ::set) }

    @Test
    fun update() {
        val gyro = optionalVariableOf(false)

        gyro.update { !it }
        gyro.get().let {
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