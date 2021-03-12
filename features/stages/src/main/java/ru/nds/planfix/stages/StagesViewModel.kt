package ru.nds.planfix.stages

import androidx.lifecycle.LiveData
import ru.nds.planfix.base.BaseViewModel
import ru.nds.planfix.models.HandbookRecord

interface StagesViewModel : BaseViewModel {

    val stages: LiveData<List<HandbookRecord>>

    fun sendStatus(position: Int)
}