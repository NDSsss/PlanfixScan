package ru.nds.planfix.selecttask

import org.koin.android.viewmodel.ext.android.viewModel
import ru.nds.planfix.base.BaseFragment
import ru.nds.planfix.binding.viewBinding
import ru.nds.planfix.selecttask.databinding.FragSelectTaskBinding

class SelectTaskFragment :
    BaseFragment<SelectTaskViewModel>(R.layout.frag_select_task) {

    companion object {
        const val TAG = "SelectTaskFragment"
        fun newInstance() = SelectTaskFragment()
    }

    override val viewModel: SelectTaskViewModel by viewModel<SelectTaskViewModelImpl>()

    private val viewBiding: FragSelectTaskBinding by viewBinding()
}

internal sealed class SelectTaskScreenState {
    object NotAuthorized : SelectTaskScreenState()
    object Loading : SelectTaskScreenState()
    object ShowTasks : SelectTaskScreenState()
}