package ru.nds.planfix.scan.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.android.viewmodel.ext.android.viewModel
import ru.nds.planfix.scan.R
import ru.nds.planfix.scan.databinding.FragmentProductsBinding
import ru.nds.planfix.scan.ui.main.MainActivity

class ProductsFragment : Fragment(R.layout.fragment_products) {

    companion object {
        const val TAG = "MainFragment"
        fun newInstance() = ProductsFragment()
    }

    private var binding: FragmentProductsBinding? = null

    private val viewModel: ProductsViewModel by viewModel<ProductsViewModelImpl>()
    private val codesAdapter = CodesAdapter(object : ICodeDeleteListener {
        override fun onProductDelete(position: Int) {
            viewModel.onProductDelete(position)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductsBinding.bind(view)
        binding?.codes?.adapter = codesAdapter
        binding?.scan?.setOnClickListener { viewModel.openScanner() }
        binding?.send?.setOnClickListener {
            viewModel.sendParsingToPlanFix()
        }

        viewModel.productsList.observe(viewLifecycleOwner) { codesAdapter.codes = it }
    }

    override fun onDestroyView() {
        binding?.codes?.adapter = null
        binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        (activity as? MainActivity)?.codeScannedListener = null
        super.onDestroy()
    }

}