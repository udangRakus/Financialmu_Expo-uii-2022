package com.example.financialmu_1.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.financialmu_1.adapter.TranscationAdapter
import com.example.financialmu_1.databinding.ActivityTransactionBinding
import com.example.financialmu_1.fragment.DateFragment
import com.example.financialmu_1.model.Transaction
import com.example.financialmu_1.preference.PreferenceManager
import com.example.financialmu_1.util.PrefUtil
import com.example.financialmu_1.util.stringToTimestamp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TransactionActivity : BaseActivity() {
    private val binding by lazy { ActivityTransactionBinding.inflate(layoutInflater) }
    private val db by lazy { Firebase.firestore }
    private val pref by lazy { PreferenceManager(this) }
    private lateinit var transactionAdapter: TranscationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUplist()
        setUplistener()
    }

    override fun onStart() {
        super.onStart()
        getTransaction()
    }

    private fun setUplistener() {
        binding.imgDate.setOnClickListener {
            DateFragment(object : DateFragment.dateListener{
                override fun onSucces(dateStart: String, dateEnd: String) {
                    Log.e("TransactionActivity", "$dateStart $dateEnd")
                    db.collection("transaction")
                        .orderBy("created", Query.Direction.DESCENDING)
                        .whereEqualTo("username", pref.getString(PrefUtil.pref_username))
                        .whereGreaterThanOrEqualTo("created", stringToTimestamp("$dateStart 00:00")!!)
                        .whereLessThanOrEqualTo("created", stringToTimestamp("$dateEnd 23:59")!!)
                        .get()
                        .addOnSuccessListener { result->
                            binding.progress.visibility = View.GONE
                            setTransaction(result)
                        }
                }

            }).apply {
                show(supportFragmentManager,"dateFragment")
            }
        }
    }


    private fun setUplist() {
        transactionAdapter = TranscationAdapter(arrayListOf(), object: TranscationAdapter.AdapterListener{
            override fun onClick(transaction: Transaction) {
                startActivity(
                    Intent(this@TransactionActivity, UpdateActivity::class.java)
                        .putExtra("id", transaction.id)
                )
            }
            override fun onLongClick(transaction: Transaction){
                val alertDialog = AlertDialog.Builder(this@TransactionActivity)
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

    private fun getTransaction() {
        binding.progress.visibility = View.VISIBLE
        db.collection("transaction")
            .orderBy("created", Query.Direction.DESCENDING)
            .whereEqualTo("username", pref.getString(PrefUtil.pref_username))
            .limit(50)
            .get()
            .addOnSuccessListener { result->
                binding.progress.visibility = View.GONE
                setTransaction(result)
            }
    }

    private fun setTransaction(result: QuerySnapshot){
        val transactions: ArrayList<Transaction> = arrayListOf()
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

    private fun deleteTransaction(id: String){
        db.collection("transaction")
            .document()
            .delete()
            .addOnSuccessListener { getTransaction() }
    }
}