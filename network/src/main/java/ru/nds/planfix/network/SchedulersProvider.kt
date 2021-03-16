package ru.nds.planfix.network

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

interface SchedulersProvider {
    fun io(): Scheduler
    fun mainThread(): Scheduler
}

class SchedulersProviderImpl(

) : SchedulersProvider {
    override fun io(): Scheduler = Schedulers.io()

    override fun mainThread(): Scheduler = AndroidSchedulers.mainThread()
}