package io.lamart.glyph

import arrow.core.Option
import io.lamart.gyro.OptionalVariable
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OptionalVariableTests {

    private fun <T> optionalVariableOf(value: T) =
        AtomicReference(value).run {
            OptionalVariable({
                Option.just(
                    get()
                )
            }, ::set)
        }

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