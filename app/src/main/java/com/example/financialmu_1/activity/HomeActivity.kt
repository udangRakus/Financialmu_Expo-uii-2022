package com.example.financialmu_1.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import com.example.financialmu_1.databinding.ActivityHomeBinding
import com.example.financialmu_1.databinding.HomeAvatarBinding
import com.example.financialmu_1.databinding.HomeDashboardBinding
import com.example.financialmu_1.model.Category
import com.example.financialmu_1.preference.PreferenceManager
import com.example.financialmu_1.util.PrefUtil
import com.example.financialmu_1.util.amountFormat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.financialmu_1.adapter.TranscationAdapter
import com.example.financialmu_1.model.Transaction
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class HomeActivity : BaseActivity() {
    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private val db by lazy { Firebase.firestore }
    private val pref by lazy { PreferenceManager(this) }
    private lateinit var transactionAdapter: TranscationAdapter
    private lateinit var bindingAvatar: HomeAvatarBinding
    private lateinit var bindingDashboard: HomeDashboardBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpBinding()
        setUplist()
        setUpListener()
    }

    private fun setUplist() {
        transactionAdapter = TranscationAdapter(arrayListOf(), object: TranscationAdapter.AdapterListener{
            override fun onClick(transaction: Transaction) {
                startActivity(
                    Intent(this@HomeActivity, UpdateActivity::class.java)
                        .putExtra("id", transaction.id)
                )
            }
            override fun onLongClick(transaction: Transaction){
                val alertDialog = AlertDialog.Builder(this@HomeActivity)
                alertDialog.apply {
                    setTitle("Hapus")
                    setMessage("Hapus ${transaction.note} dari histori transaksi")
                    setNegativeButton("Batal"){ dialogInterface,_ ->
                        dialogInterface.dismiss()
                    }
                    setPositiveButton("Hapus"){ dialogInterface,_ ->
                        deleteTransaction(transaction.id!!)
                        dialogInterface.dismiss()
                    }
                }
                alertDialog.show()
            }
        })
        binding.listTransaction.adapter = transactionAdapter
    }

    override fun onStart() {
        super.onStart()
        getAvatar()
        getBalance()
        getTransaction()
    }

    private fun getAvatar() {
        bindingAvatar.textAvatar.text = pref.getString(PrefUtil.pref_username)
    }

    private fun getBalance() {
        var totalBalance = 0
        var totalIn = 0
        var totalOut = 0
        db.collection("transaction")
            .whereEqualTo("username", pref.getString(PrefUtil.pref_username))
            .get()
            .addOnSuccessListener { result->
                result.forEach { doc->
                    totalBalance += doc.data["amount"].toString().toInt()
                    when(doc.data["type"].toString()){
                        "IN" -> totalIn += doc.data["amount"].toString().toInt()
                        "OUT" -> totalOut += doc.data["amount"].toString().toInt()
                    }
                }
                bindingDashboard.textBalanced.text = amountFormat(totalBalance)
                bindingDashboard.textIn.text = amountFormat(totalIn)
                bindingDashboard.textOut.text = amountFormat(totalOut)
            }
    }

    private fun getTransaction() {
        binding.progress.visibility = View.VISIBLE
        val transactions: ArrayList<Transaction> = arrayListOf()
        db.collection("transaction")
            .orderBy("created", Query.Direction.DESCENDING)
            .whereEqualTo("username", pref.getString(PrefUtil.pref_username))
            .limit(5)
            .get()
            .addOnSuccessListener { result->
                binding.progress.visibility = View.GONE
                result.forEach { doc->
                    transactions.add(
                        Transaction(
                            id = doc.reference.id,
                            username = doc.data["username"].toString(),
                            category = doc.data["category"].toString(),
                            amount = doc.data["amount"].toString().toInt(),
                            type = doc.data["type"].toString(),
                            note = doc.data["note"].toString(),
                            created = doc.data["created"] as Timestamp

                        )
                    )

                }
                transactionAdapter.setData(transactions)
            }
    }




    private fun setUpListener() {
        bindingAvatar.imgAvatar.setOnClickListener{
            startActivity(
                Intent(this, ProfileActivity::class.java)
                    .putExtra("balance",bindingDashboard.textBalanced.text.toString())
            )
        }
        binding.fabCreate.setOnClickListener{
            startActivity(Intent(this, CreateActivity::class.java))
        }
        binding.textTransaction.setOnClickListener{
            startActivity(Intent(this, TransactionActivity::class.java))
        }
    }

    private fun setUpBinding(){
        setContentView(binding.root)
        bindingAvatar = binding.includeAvatar
        bindingDashboard = binding.includeDashboard
    }

    private fun deleteTransaction(id: String){
        db.collection("transaction")
            .document()
            .delete()
            .addOnSuccessListener {
                getTransaction()
                getBalance()
            }
    }
}