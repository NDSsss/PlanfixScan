package ru.nds.planfix.scan.ui.codes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import ru.nds.planfix.scan.R
import ru.nds.planfix.scan.databinding.ItemCodeBinding
import ru.nds.planfix.scan.models.CodeModel

class CodesAdapter : RecyclerView.Adapter<CodeVh>() {
    var codes: MutableList<CodeModel> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodeVh =
        CodeVh(LayoutInflater.from(parent.context).inflate(R.layout.item_code, parent, false))

    override fun onBindViewHolder(holder: CodeVh, position: Int) {
        holder.bindCode(code = codes[position], object : CodeVh.ICodeDeleteListener {
            override fun onCodeDelete(position: Int) {
                codes.removeAt(position)
                notifyDataSetChanged()
            }
        })
    }

    override fun getItemCount(): Int = codes.size
}

class CodeVh(view: View) : RecyclerView.ViewHolder(view) {
    fun bindCode(code: CodeModel, codeDeleteListener: ICodeDeleteListener) {
        val binding = ItemCodeBinding.bind(itemView)
        binding.name.text = "${code.code} ${code.state}"
        binding.price.setText(code.price.toString())
        binding.price.doAfterTextChanged { code.price = it.toString().toIntOrNull() ?: 0 }
        binding.delete.setOnClickListener {
            codeDeleteListener.onCodeDelete(adapterPosition)
        }
    }

    interface ICodeDeleteListener {
        fun onCodeDelete(position: Int)
    }
}