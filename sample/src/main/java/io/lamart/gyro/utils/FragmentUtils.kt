package io.lamart.gyro.utils

import androidx.fragment.app.Fragment

fun <T> Fragment.component(block: Component.() -> T): Lazy<T> =
    lazy {
        requireContext()
            .applicationContext
            .let { it as Component }
            .run(block)
    }
