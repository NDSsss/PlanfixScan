package ru.nds.planfix.chooser

import android.os.Bundle
import android.view.View
import org.koin.android.viewmodel.ext.android.viewModel
import ru.nds.planfix.base.BaseFragment
import ru.nds.planfix.binding.viewBinding
import ru.nds.planfix.chooser.databinding.FragChooserBinding

class ChooserFragment : BaseFragment<ChooserFragmentViewModel>(R.layout.frag_chooser) {

    companion object {
        const val TAG = "ChooserFragment"
        fun newInstance() = ChooserFragment()
    }

    override val viewModel: ChooserFragmentViewModel by viewModel<ChooserFragmentViewModelImpl>()

    private val binding: FragChooserBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toProducts.setOnClickListener {
            viewModel.openProductsScan()
        }
        binding.toStages.setOnClickListener {
            viewModel.openStagesScan()
        }
    }
}