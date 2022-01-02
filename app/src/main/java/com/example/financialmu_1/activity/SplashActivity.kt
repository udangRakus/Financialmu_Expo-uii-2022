package com.example.financialmu_1.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.financialmu_1.R
import com.example.financialmu_1.preference.PreferenceManager

class SplashActivity : BaseActivity() {
    private val pref by lazy { PreferenceManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            if (pref.getInt("pref_is_login") == 0){
                startActivity(Intent(this, LoginActivity::class.java))
            }else startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, 2000)
    }
}