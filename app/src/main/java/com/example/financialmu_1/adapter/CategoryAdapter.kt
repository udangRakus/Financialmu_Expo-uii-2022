package com.example.financialmu_1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.financialmu_1.R
import com.example.financialmu_1.databinding.AdapterCategoryBinding
import com.example.financialmu_1.model.Category
import com.google.android.material.button.MaterialButton

class CategoryAdapter(
    val context: Context,
    var categories: ArrayList<Category>,
    var listener: AdapterListener?
): RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){
    private var listButton: ArrayList<MaterialButton> = arrayListOf()
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        return ViewHolder(
            AdapterCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.btnCategory.text = category.name

        holder.binding.btnCategory.setOnClickListener{
            listener?.onClick(category)
            setButton(it as MaterialButton)
        }
        listButton.add(holder.binding.btnCategory)
    }

    override fun getItemCount() = categories.size

    public fun setData(data: List<Category>){
        categories.clear()
        categories.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: AdapterCategoryBinding): RecyclerView.ViewHolder(binding.root)

    interface AdapterListener{
        fun onClick(category: Category)
    }

    private fun setButton(buttonSelected: MaterialButton) {
        listButton.forEach{ button->
            button.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))

            }
        buttonSelected.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_200))
        }

    public fun setButton(category: String){
        listButton.forEach{ button->
            if(button.text.toString().contains(category)){
                button.setBackgroundColor(ContextCompat.getColor(context,R.color.purple_200))
            }
        }

    }

}


