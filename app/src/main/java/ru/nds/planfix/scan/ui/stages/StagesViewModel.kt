package ru.nds.planfix.scan.ui.stages

import androidx.lifecycle.LiveData
import ru.nds.planfix.scan.models.HandbookRecord

interface StagesViewModel {

    val stages: LiveData<List<HandbookRecord>>

    fun sendStatus(position: Int)
}