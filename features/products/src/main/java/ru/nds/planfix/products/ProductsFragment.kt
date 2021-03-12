package ru.nds.planfix.products

import android.os.Bundle
import android.view.View
import org.koin.android.viewmodel.ext.android.viewModel
import ru.nds.planfix.base.BaseFragment
import ru.nds.planfix.binding.viewBinding
import ru.nds.planfix.products.databinding.FragmentProductsBinding

class ProductsFragment : BaseFragment<ProductsViewModel>(R.layout.fragment_products) {

    companion object {
        const val TAG = "MainFragment"
        fun newInstance() = ProductsFragment()
    }

    private val binding: FragmentProductsBinding by viewBinding()

    override val viewModel: ProductsViewModel by viewModel<ProductsViewModelImpl>()

    private val codesAdapter = CodesAdapter(object : ICodeDeleteListener {
        override fun onProductDelete(position: Int) {
            viewModel.onProductDelete(position)
        }
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.codes.adapter = codesAdapter
        binding.scan.setOnClickListener { viewModel.openScanner() }
        binding.send.setOnClickListener {
            viewModel.sendParsingToPlanFix()
        }

        viewModel.productsList.observe(viewLifecycleOwner) { codesAdapter.codes = it }
    }

    override fun onDestroyView() {
        binding.codes.adapter = null
        super.onDestroyView()
    }

}