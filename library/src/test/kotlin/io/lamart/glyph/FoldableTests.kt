package io.lamart.glyph

import io.lamart.gyro.Foldable
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FoldableTests {

    @Test
    fun some() {
        Foldable
            .some { true }
            .fold({ false }, { it })
            .let { assertTrue(it) }
    }

    @Test
    fun none() {
        Foldable
            .none<Boolean>()
            .fold({ false }, { it })
            .let { assertFalse(it) }
    }

    @Test
    fun maybe() {
        Foldable
            .maybe { true }
            .fold({ false }, { it })
            .let { assertTrue(it) }

        Foldable
            .maybe<Boolean> { null }
            .fold({ false }, { it })
            .let { assertFalse(it) }
    }

}