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
import ru.nds.planfix.scan.SettingsQrScannedListener
import ru.nds.planfix.scan.data.PrefsStorage
import ru.nds.planfix.scan.databinding.MainFragmentBinding

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

    private val settingsQrScannedListener = object : SettingsQrScannedListener {
        override fun onSettingsQrScanned(configJson: String) {
            viewModel.onSettingsQrScanned(configJson)
        }

    }

    private var binding: MainFragmentBinding? = null

    private val vmBinds = CompositeDisposable()

    private lateinit var viewModel: CodesViewModel

    private val codesAdapter = CodesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as? MainActivity)?.codeScannedListener = codeScannedListener
        (activity as? MainActivity)?.settingsQrScannedListener = settingsQrScannedListener
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
        viewModel.prefs = PrefsStorage(requireActivity().applicationContext)
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
        binding?.scan?.setOnClickListener { (activity as? MainActivity)?.openScanner() }
        binding?.send?.setOnClickListener {
            (activity as? MainActivity)?.codesList?.also {
                viewModel.sendParsingToPlanFix(it)
            }
        }
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
        (activity as? MainActivity)?.settingsQrScannedListener = null
        vmBinds.dispose()
        super.onDestroy()
    }

}