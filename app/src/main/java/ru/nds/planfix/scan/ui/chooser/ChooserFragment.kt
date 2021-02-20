package ru.nds.planfix.scan.ui.chooser

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ru.nds.planfix.scan.R
import ru.nds.planfix.scan.databinding.FragChooserBinding
import ru.nds.planfix.scan.ui.codes.CodesFragment
import ru.nds.planfix.scan.ui.status.SendStatusFragment
import ru.nds.planfix.scan.ui.utils.viewBinding

class ChooserFragment : Fragment(R.layout.frag_chooser) {

    companion object {
        const val TAG = "ChooserFragment"
        fun newInstance() = ChooserFragment()
    }

    private val binding: FragChooserBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toProducts.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .addToBackStack(TAG)
                .replace(R.id.container, CodesFragment.newInstance(), CodesFragment.TAG)
                .commit()
        }
        binding.toStages.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .addToBackStack(TAG)
                .replace(R.id.container, SendStatusFragment.newInstance(), SendStatusFragment.TAG)
                .commit()
        }
    }
}