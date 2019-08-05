package io.lamart.gyro.rxjava2

import io.lamart.gyro.Foldable
import io.lamart.gyro.mutable.OptionalMutable
import io.lamart.gyro.mutable.mutableOfNullable
import io.lamart.gyro.observable.Sender
import io.lamart.gyro.variables.OptionalVariable
import io.lamart.gyro.variables.optionalVariableOf
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposables
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

fun <T> Sender<T>.toObservable(): Observable<T> =
    object : Observable<T>() {

        override fun subscribeActual(observer: Observer<in T>) {
            this@toObservable
                .subscribe(observer::onNext)
                .run { Disposables.fromAction(::unsubscribe) }
                .let(observer::onSubscribe)
        }

    }