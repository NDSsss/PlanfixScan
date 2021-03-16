package ru.nds.planfix.network

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

//TODO: move internet utils to separate module

fun Throwable.isInternetError(): Boolean {
    return when (this) {
        is UnknownHostException -> true
        is TimeoutException -> true
        else -> false
    }
}

fun <T : Any> Single<T>.subToThreads(schedulersProvider: SchedulersProvider): Single<T> =
    this.subscribeOn(schedulersProvider.io())
        .observeOn(schedulersProvider.mainThread())
fun Completable.subToThreads(schedulersProvider: SchedulersProvider): Completable =
    this.subscribeOn(schedulersProvider.io())
        .observeOn(schedulersProvider.mainThread())