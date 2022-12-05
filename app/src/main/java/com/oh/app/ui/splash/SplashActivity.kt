package com.oh.app.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.oh.app.R
import com.oh.app.ui.main.MainActivity


class SplashActivity : AppCompatActivity() {
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler = Handler(Looper.getMainLooper())
    }

    override fun onStart() {
        super.onStart()
        handler.postDelayed({ beginMainActivity() }, 2000)
    }

    fun onClick(view: View?) {
        beginMainActivity()
    }

    private fun beginMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}