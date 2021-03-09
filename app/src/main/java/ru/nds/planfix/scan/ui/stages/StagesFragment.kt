package ru.nds.planfix.scan.ui.stages

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import org.koin.android.viewmodel.ext.android.viewModel
import ru.nds.planfix.scan.R
import ru.nds.planfix.scan.databinding.FragmentStagesBinding
import ru.nds.planfix.scan.ui.utils.viewBinding

class StagesFragment : Fragment(R.layout.fragment_stages) {

    companion object {
        const val TAG = "SendStatusFragment"
        fun newInstance() = StagesFragment()
    }

    private val binding: FragmentStagesBinding by viewBinding()

    private val viewModel: StagesViewModel by viewModel<StagesViewModelImpl>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.send.setOnClickListener {
            viewModel.sendStatus(binding.stages.selectedItemPosition)
        }
        viewModel.stages.observe(viewLifecycleOwner) { stages ->
            initStages(stages.map { record ->
                record.customData.getOrNull(0)?.text ?: "parsing error"
            })
        }
    }

    private fun initStages(stages: List<String>) {
        binding.stages.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            stages
        )
    }

}