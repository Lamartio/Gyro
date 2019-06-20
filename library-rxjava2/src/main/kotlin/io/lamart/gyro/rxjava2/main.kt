package io.lamart.gyro.rxjava2

import io.lamart.gyro.observable.Sender
import io.lamart.gyro.segment.OptionalSegment
import io.lamart.gyro.segment.segmentOfNullable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject

fun <T> BehaviorSubject<T>.toOptionalSegment(): OptionalSegment<T> =
    segmentOfNullable({ value }, ::onNext)

fun <T> ReplaySubject<T>.toOptionalSegment(): OptionalSegment<T> =
    segmentOfNullable({ value }, ::onNext)

fun <T> Sender<T>.toObservable(): Observable<T> =
    object : Observable<T>() {

        override fun subscribeActual(observer: Observer<in T>) {
            this@toObservable
                .subscribe(observer::onNext)
                .run { Disposables.fromAction(::unsubscribe) }
                .let(observer::onSubscribe)
        }

    }