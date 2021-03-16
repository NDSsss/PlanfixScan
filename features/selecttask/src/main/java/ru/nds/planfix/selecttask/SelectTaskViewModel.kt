package ru.nds.planfix.selecttask

import androidx.lifecycle.LiveData
import ru.nds.planfix.base.BaseViewModel
import ru.nds.planfix.selecttask.domain.TaskEntity
import ru.nds.planfix.selecttask.domain.TaskStatusEntity

interface SelectTaskViewModel : BaseViewModel {

    val tasks: LiveData<List<TaskEntity>>
    val statuses: LiveData<List<TaskStatusEntity>>

    fun onTaskSelected(position: Int)
    fun onStatusSelected(position: Int)

    fun onSaveClick(taskPosition: Int, statusPosition: Int)
    fun reload()
}