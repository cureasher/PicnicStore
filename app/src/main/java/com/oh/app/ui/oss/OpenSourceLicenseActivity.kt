package com.oh.app.ui.oss

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.oh.app.databinding.ActivityOpensourceLicenseBinding

/**
 * 오픈소스 라이센스 Activity
 */
class OpenSourceLicenseActivity : AppCompatActivity(){
    private lateinit var binding : ActivityOpensourceLicenseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpensourceLicenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}