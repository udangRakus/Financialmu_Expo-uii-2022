package com.example.financialmu_1.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.financialmu_1.R
import com.example.financialmu_1.adapter.CategoryAdapter
import com.example.financialmu_1.databinding.ActivityCreateBinding
import com.example.financialmu_1.model.Category
import com.example.financialmu_1.model.Transaction
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UpdateActivity : BaseActivity() {
    private final val TAG: String = "UpdateActivity"
    private val db by lazy { Firebase.firestore }

    private val binding by lazy { ActivityCreateBinding.inflate(layoutInflater) }
    private val transactionId by lazy{intent.getStringExtra("id")}
    private lateinit var categorAdapter: CategoryAdapter
    private lateinit var transaction: Transaction
    private var type: String = ""
    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUplist()
        setUplistener()
        Log.e(TAG, "transactionId : $transactionId")
    }

    override fun onStart() {
        super.onStart()
        detailTransaction()
    }

    private fun setUplist() {
        categorAdapter = CategoryAdapter(this,arrayListOf(), object: CategoryAdapter.AdapterListener{
            override fun onClick(category: Category) {
                transaction.category = category.name!!
            }
        })
        binding.listCategory.adapter = categorAdapter
    }

    private fun setUplistener() {
        binding.btnSave.setText("simpan perubahan")
        binding.btnSave.setOnClickListener{
            transaction.amount = binding.editAmount.text.toString().toInt()
            transaction.note = binding.editNote.text.toString()
            db.collection("transaction")
                .document(transactionId!!)
                .set(transaction)
                .addOnSuccessListener { result->
                    Toast.makeText(applicationContext, "Trasaksi diubah", Toast.LENGTH_SHORT).show()
                    finish()
                }
            binding.btnIn.setOnClickListener{
                transaction.type = "IN"
                setButton(it as MaterialButton)
            }
            binding.btnOut.setOnClickListener {
                transaction.type = "OUT"
                setButton(it as MaterialButton)
            }
        }
    }

    private fun setButton(buttonSelected: MaterialButton){
        Log.e(TAG, type)
        listOf<MaterialButton>(binding.btnIn, binding.btnOut).forEach {
            it.setBackgroundColor(ContextCompat.getColor(this,android.R.color.darker_gray))
        }
        buttonSelected.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_200))
    }

    private fun getCategory() {
        val categories: ArrayList<Category> = arrayListOf()
        db.collection("category")
            .get()
            .addOnSuccessListener { result->
                result.forEach{ document->
                    categories.add(Category(document.data["name"].toString()))
                }
                Log.e("HomeActivity", "Categories : $categories")
                categorAdapter.setData(categories)
                Handler(Looper.myLooper()!!).postDelayed({
                    categorAdapter.setButton(transaction.category)
                },200)
            }
    }

    private fun detailTransaction(){
        db.collection("transaction")
            .document(transactionId!!)
            .get()
            .addOnSuccessListener { result->
                transaction = Transaction(
                    id = result.id,
                    amount = result["amount"].toString().toInt(),
                    category = result["category"].toString(),
                    type = result["type"].toString(),
                    note = result["note"].toString(),
                    username = result["username"].toString(),
                    created = result["created"] as Timestamp
                )
                binding.editAmount.setText(transaction.amount.toString())
                binding.editNote.setText(transaction.note.toString())

                when(transaction.type){
                    "IN" -> setButton(binding.btnIn)
                    "OUT" -> setButton(binding.btnOut)
                }
                getCategory()
            }
    }
}