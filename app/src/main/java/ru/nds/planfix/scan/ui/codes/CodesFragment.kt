package ru.nds.planfix.scan.ui.codes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ru.nds.planfix.scan.CodeScannedListener
import ru.nds.planfix.scan.MainActivity
import ru.nds.planfix.scan.R
import ru.nds.planfix.scan.data.PrefsStorage
import ru.nds.planfix.scan.data.ProductsPrefs
import ru.nds.planfix.scan.databinding.MainFragmentBinding
import ru.nds.planfix.scan.ui.scanner.ScannerFragment

class CodesFragment : Fragment() {

    companion object {
        const val TAG = "MainFragment"
        fun newInstance() = CodesFragment()
    }

    private val codeScannedListener = object : CodeScannedListener {
        override fun onCodeScanned(code: String) {
            viewModel.onCodeScanned(code)
        }
    }

    private var binding: MainFragmentBinding? = null

    private val vmBinds = CompositeDisposable()

    private lateinit var viewModel: CodesViewModel

    private val codesAdapter = CodesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as? MainActivity)?.codeScannedListener = codeScannedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CodesViewModel::class.java)
        viewModel.prefs = ProductsPrefs(requireActivity().applicationContext)
        vmBinds.addAll(
            viewModel.codeParsedSubject.subscribe {
                (activity as? MainActivity)?.codesList?.apply {
                    add(it)
                    codesAdapter.codes = this
                }
            },
            viewModel.actionSuccessSubject.subscribe {
                Toast.makeText(requireContext(), "Успешно", Toast.LENGTH_SHORT).show()
            },
            viewModel.clearCodesSubject.subscribe {
                (activity as? MainActivity)?.codesList?.clear()
                codesAdapter.notifyDataSetChanged()
            },
            viewModel.actionFailSubject.subscribe { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MainFragmentBinding.bind(view)
        binding?.codes?.adapter = codesAdapter
        binding?.scan?.setOnClickListener { openScanner() }
        binding?.send?.setOnClickListener {
            (activity as? MainActivity)?.codesList?.also {
                viewModel.sendParsingToPlanFix(it)
            }
        }
    }



    private fun openScanner() {
        parentFragmentManager.beginTransaction()
            .addToBackStack("openScanner")
            .replace(R.id.container, ScannerFragment.newInstance(), ScannerFragment.TAG)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.codesList?.also { actualCodes ->
            codesAdapter.codes = actualCodes
        }
    }

    override fun onDestroyView() {
        binding?.codes?.adapter = null
        binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        (activity as? MainActivity)?.codeScannedListener = null
        vmBinds.dispose()
        super.onDestroy()
    }

}