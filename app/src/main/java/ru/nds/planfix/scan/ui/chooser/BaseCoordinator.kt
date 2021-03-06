package ru.nds.planfix.scan.ui.chooser

import io.reactivex.rxjava3.core.Observable

interface BaseCoordinator {
    fun back()
    fun <T> addResultListener(code: Int): Observable<T>
    fun removeResultListener(code: Int)
    fun sendResult(code: Int, result: Any?): Boolean
}