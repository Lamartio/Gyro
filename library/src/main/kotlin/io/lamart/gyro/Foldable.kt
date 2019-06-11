package io.lamart.gyro

import arrow.core.Option

interface Foldable<T> {

    fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R

    companion object {

        fun <T> some(get: () -> T) = object : Foldable<T> {

            override fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R = ifSome(get())

        }

        fun <T> none() = object : Foldable<T> {

            override fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R = ifNone()

        }

        fun <T> maybe(get: () -> Option<T>) = object : Foldable<T> {
            
            override fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R = get().fold(ifNone, ifSome)

        }

    }

}

fun <T> Foldable<T>.getOrElse(default: T): T = fold({ default }, { it })

fun <T> Foldable<T>.getOrNull(): T? = fold({ null }, { it })

fun <T> Option<T>.toFoldable(): Foldable<T> =
    object : Foldable<T> {

        override fun <R> fold(ifNone: () -> R, ifSome: (T) -> R): R = this@toFoldable.fold(ifNone, ifSome)

    }
