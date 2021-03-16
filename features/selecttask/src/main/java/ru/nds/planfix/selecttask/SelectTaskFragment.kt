package ru.nds.planfix.selecttask

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import org.koin.android.viewmodel.ext.android.viewModel
import ru.nds.planfix.base.BaseFragment
import ru.nds.planfix.binding.viewBinding
import ru.nds.planfix.selecttask.databinding.FragSelectTaskBinding
import ru.nds.planfix.selecttask.domain.TaskEntity
import ru.nds.planfix.selecttask.domain.TaskStatusEntity

class SelectTaskFragment :
    BaseFragment<SelectTaskViewModel>(R.layout.frag_select_task) {

    companion object {
        const val TAG = "SelectTaskFragment"
        fun newInstance() = SelectTaskFragment()
    }

    override val viewModel: SelectTaskViewModel by viewModel<SelectTaskViewModelImpl>()

    private val viewBiding: FragSelectTaskBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.tasks.observe(viewLifecycleOwner, ::initTasks)
        viewModel.statuses.observe(viewLifecycleOwner, ::initStatuses)

        viewBiding.spTasks.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.onTaskSelected(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }
        viewBiding.spStatuses.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.onStatusSelected(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }
        viewBiding.btnConfirm.setOnClickListener {
            viewModel.onSaveClick(
                viewBiding.spTasks.selectedItemPosition,
                viewBiding.spStatuses.selectedItemPosition
            )
        }
    }

    private fun initTasks(tasks: List<TaskEntity>) {
        viewBiding.spTasks.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            tasks.map { task -> task.name }
        )
    }

    private fun initStatuses(statuses: List<TaskStatusEntity>) {
        viewBiding.spStatuses.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statuses.map { status -> status.name }
        )
    }
}

internal sealed class SelectTaskScreenState {
    object NotAuthorized : SelectTaskScreenState()
    object Loading : SelectTaskScreenState()
    object ShowTasks : SelectTaskScreenState()
}