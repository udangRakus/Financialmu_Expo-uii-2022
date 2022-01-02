package com.example.financialmu_1.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.financialmu_1.R
import com.example.financialmu_1.adapter.CategoryAdapter
import com.example.financialmu_1.adapter.TranscationAdapter
import com.example.financialmu_1.databinding.ActivityCreateBinding
import com.example.financialmu_1.model.Category
import com.example.financialmu_1.model.Transaction
import com.example.financialmu_1.model.User
import com.example.financialmu_1.preference.PreferenceManager
import com.example.financialmu_1.util.PrefUtil
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateActivity : BaseActivity() {
    final val TAG: String = "CreateActivity"

    private val binding by lazy { ActivityCreateBinding.inflate(layoutInflater) }
    private val db by lazy { Firebase.firestore }
    private val pref by lazy { PreferenceManager(this) }

    private lateinit var categorAdapter: CategoryAdapter
    private var category: String = ""
    private var type: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUplist()
        setUplistener()
    }

    private fun setUplistener() {
        binding.btnIn.setOnClickListener{
            type = "IN"
            setButton(it as MaterialButton)
        }
        binding.btnOut.setOnClickListener {
            type = "OUT"
            setButton(it as MaterialButton)
        }
        binding.btnSave.setOnClickListener {
            val transaction = Transaction(
                id = null,
                username = pref.getString(PrefUtil.pref_username)!!,
                category = category,
                type = type,
                amount = binding.editAmount.text.toString().toInt(),
                note = binding.editNote.text.toString(),
                created = Timestamp.now()

            )
            db.collection("transaction")
                .add(transaction)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "transaksi disimpan", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    private fun setButton(buttonSelected: MaterialButton){
        Log.e(TAG, type)
        listOf<MaterialButton>(binding.btnIn, binding.btnOut).forEach {
            it.setBackgroundColor(ContextCompat.getColor(this,android.R.color.darker_gray))
        }
        buttonSelected.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
    }

    override fun onStart() {
        super.onStart()
        getCategory()
    }

    private fun setUplist() {
        categorAdapter = CategoryAdapter(this,arrayListOf(), object: CategoryAdapter.AdapterListener{
            override fun onClick(category: Category) {
                this@CreateActivity.category = category.name!!
                Log.e(TAG, this@CreateActivity.category)
            }
        })
        binding.listCategory.adapter = categorAdapter
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
            }
    }
}