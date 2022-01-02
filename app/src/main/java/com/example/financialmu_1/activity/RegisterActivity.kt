package com.example.financialmu_1.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.financialmu_1.databinding.ActivityRegisterBinding
import com.example.financialmu_1.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : BaseActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private val db by lazy { Firebase.firestore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUplistener()
    }

    private fun setUplistener() {
        binding.textLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()

        }
        binding.btnRegister.setOnClickListener{
            addUSer()
        }
    }

    private fun addUSer() {
        val user = User(
            email = binding.editEmail.text.toString(),
            username = binding.editUsername.text.toString(),
            password = binding.editPassword.text.toString(),
            created = Timestamp.now()
        )
        db.collection("user")
            .add(user)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Data disimpan", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
    }


}