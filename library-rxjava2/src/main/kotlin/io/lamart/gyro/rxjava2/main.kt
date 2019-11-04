package io.lamart.gyro.rxjava2

import io.lamart.gyro.Foldable
import io.lamart.gyro.mutable.OptionalMutable
import io.lamart.gyro.mutable.mutableOfNullable
import io.lamart.gyro.variables.OptionalVariable
import io.lamart.gyro.variables.optionalVariableOf
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject

fun <T> BehaviorSubject<T>.toOptionVariable(): OptionalVariable<T> =
    optionalVariableOf({ Foldable.maybe { value } }, ::onNext)

fun <T> BehaviorSubject<T>.toOptionalMutable(): OptionalMutable<T> =
    mutableOfNullable({ value }, ::onNext)

fun <T> ReplaySubject<T>.toOptionalMutable(): OptionalMutable<T> =
    mutableOfNullable({ value }, ::onNext)

fun <T> ReplaySubject<T>.toOptionVariable(): OptionalVariable<T> =
    optionalVariableOf({ Foldable.maybe { value } }, ::onNext)
