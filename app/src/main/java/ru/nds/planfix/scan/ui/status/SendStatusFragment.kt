package ru.nds.planfix.scan.ui.status

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ru.nds.planfix.scan.R
import ru.nds.planfix.scan.data.StagesPrefs
import ru.nds.planfix.scan.databinding.SendStatusFragmentBinding
import ru.nds.planfix.scan.ui.utils.viewBinding

class SendStatusFragment : Fragment(R.layout.send_status_fragment) {

    companion object {
        const val TAG = "SendStatusFragment"
        fun newInstance() = SendStatusFragment()
    }

    private val binding: SendStatusFragmentBinding by viewBinding()

    private lateinit var viewModel: SendStatusViewModel

    private val vmBinds = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SendStatusViewModel::class.java)
        viewModel.setPrefs(StagesPrefs(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.send.setOnClickListener {
            viewModel.sendStatus(binding.stages.selectedItemPosition)
        }
        vmBinds.addAll(
            viewModel.actionSuccessSubject.subscribe {
                Toast.makeText(requireContext(), "Успешно", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            },
            viewModel.stagesSubject.subscribe {
                initStages(it.map { record ->
                    record.customData.getOrNull(0)?.text ?: "parsing error"
                })
            }
        )
    }

    private fun initStages(stages: List<String>) {
        binding.stages.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            stages
        )
    }

    override fun onDestroyView() {
        vmBinds.dispose()
        super.onDestroyView()
    }

}