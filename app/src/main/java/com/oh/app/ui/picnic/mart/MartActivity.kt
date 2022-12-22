package com.oh.app.ui.picnic.mart

import com.oh.app.ui.picnic.mart.adapter.MartRecyclerAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.oh.app.common.TAG
import com.oh.app.databinding.ActivityMartBinding
import com.oh.app.rest.setting.RetrofitGenerator
import com.oh.app.ui.picnic.mart.repository.MartRepository

/**
 * 마트 ViewPager에서 -> 마트 상품 Activity
 * 현재 마트의 상품 가격 리스트 보여주는 화면
 */
class MartActivity : AppCompatActivity() {
    lateinit var binding: ActivityMartBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        martSetting()
    }

    private fun martSetting() {
        val viewModel: MartViewModel = ViewModelProvider(
            this, MartViewModelFactory(MartRepository(RetrofitGenerator.getMartInstance()))
        )[MartViewModel::class.java]
        val martIntent = intent
        var martName = martIntent.getStringExtra("name") ?: "현대시장"
        martName = martName.replace("\\s".toRegex(), "")
        viewModel.getMartViewModel(martName)

        viewModel.martList.observe(this) { martItem ->
            val martInfoList = martItem.martData.martInfoList
            binding.martName.text = intent.getStringExtra("name")
            with(binding.martList) {
                run {
                    layoutManager = LinearLayoutManager(this@MartActivity)
                    Log.d(TAG, "MartActivity: ${martInfoList[0]}")
                    val martAdapter = MartRecyclerAdapter(martItem, this@MartActivity)
                    Log.d(TAG, "MartActivity: ${martAdapter.itemCount}")
                    adapter = martAdapter
                    martAdapter
                }.refreshMartItems()
            }
        }
    }
}