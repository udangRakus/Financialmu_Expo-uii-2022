package com.example.financialmu_1.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.financialmu_1.R
import com.example.financialmu_1.databinding.ActivityLoginBinding
import com.example.financialmu_1.model.User
import com.example.financialmu_1.preference.PreferenceManager
import com.example.financialmu_1.util.timestampToString
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : BaseActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val db by lazy { Firebase.firestore }
    private val pref by lazy { PreferenceManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUplistener()
    }

    private fun setUplistener() {
        binding.textRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()

        }
        binding.btnLogin.setOnClickListener{
            db.collection("user")
                .whereEqualTo("username", binding.editUsername.text.toString())
                .whereEqualTo("password", binding.editPassword.text.toString())
                .get()
                .addOnSuccessListener { result->
                    if(result.isEmpty) binding.textAlert.visibility = View.VISIBLE
                    result.forEach { document->
                        saveSession(
                            User(
                                email = document.data["email"].toString(),
                                username = document.data["username"].toString(),
                                password = document.data["password"].toString(),
                                created = document.data["created"] as Timestamp
                            )
                        )
                    }
                    Toast.makeText(applicationContext, "Login berhasil", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
        }
    }

    private fun saveSession(user: User) {
        Log.e("LoginActivity", user.toString())
        pref.put("pref_is_login",1)
        pref.put("pref_email", user.email)
        pref.put("pref_username",user.username)
        pref.put("pref_password",user.password)
        pref.put("pref_date", timestampToString(user.created)!!)
    }
}
