package ru.nds.planfix.scan.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ru.nds.planfix.scan.R
import ru.nds.planfix.scan.data.PrefsStorage
import ru.nds.planfix.scan.databinding.SettingsFragmentBinding

class SettingsFragment : Fragment(R.layout.settings_fragment) {

    companion object {
        const val TAG = "SettingsFragment"
        fun newInstance() = SettingsFragment()
    }

    private val vmBinds = CompositeDisposable()

    private lateinit var viewModel: SettingsViewModel

    private var binding: SettingsFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(
            "APP_TAG",
            "${this::class.java.simpleName} ${this::class.java.hashCode()} onCreate: "
        );
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        Log.d(
            "APP_TAG",
            "${this::class.java.simpleName} ${this::class.java.hashCode()} onActivityCreated: "
        );
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(
            "APP_TAG",
            "${this::class.java.simpleName} ${this::class.java.hashCode()} onViewCreated: "
        );
        super.onViewCreated(view, savedInstanceState)
        binding = SettingsFragmentBinding.bind(view)
        binding?.apply {
            generateAuth.setOnClickListener {
//                viewModel.generateAuth(
//                    apiKey = etApiKey.text.toString(),
//                    token = etToken.text.toString()
//                )
            }
        }
        vmBinds.addAll(
            viewModel.authSubject.subscribe { binding?.authCode?.text = it },
            viewModel.sidSubject.subscribe { binding?.sid?.text = it },
        )
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        vmBinds.dispose()
        super.onDestroy()
    }


}