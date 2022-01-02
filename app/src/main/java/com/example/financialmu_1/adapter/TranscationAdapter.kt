package com.example.financialmu_1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financialmu_1.R
import com.example.financialmu_1.databinding.AdapterTransactionBinding
import com.example.financialmu_1.model.Transaction
import com.example.financialmu_1.util.amountFormat
import com.example.financialmu_1.util.timestampToString

class TranscationAdapter(
    var transaction: ArrayList<Transaction>,
    var listener: AdapterListener?
): RecyclerView.Adapter<TranscationAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TranscationAdapter.ViewHolder {
        return ViewHolder(
            AdapterTransactionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: TranscationAdapter.ViewHolder, position: Int) {
        val transaction = transaction[position]
        if(transaction.type.toUpperCase() == "IN") holder.binding.imgType.setImageResource(R.drawable.ic_in)
        else holder.binding.imgType.setImageResource(R.drawable.ic_out)

        holder.binding.textNote.text = transaction.note
        holder.binding.textCategory.text = transaction.category
        holder.binding.textAmount.text = amountFormat(transaction.amount)
        holder.binding.textDate.text = timestampToString(transaction.created!!)

        holder.binding.container.setOnClickListener{
            listener?.onClick(transaction)
        }

    }

    override fun getItemCount() = transaction.size

    public fun setData(data: List<Transaction>){
        transaction.clear()
        transaction.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: AdapterTransactionBinding): RecyclerView.ViewHolder(binding.root)

    interface AdapterListener{
        fun onClick(transaction: Transaction)
        fun onLongClick(transaction: Transaction)
    }

}