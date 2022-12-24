package com.oh.app.ui.picnic

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Window
import com.oh.app.common.TAG
import com.oh.app.databinding.ActivityThemaBinding
import com.oh.app.ui.main.MainActivity
import com.oh.app.ui.thema.ThemaUtil

/**
 * android 테마 변경하는 다이얼로그
 */
class PicnicDialog(owner: MainActivity) : Dialog(owner) {
    private lateinit var binding: ActivityThemaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 대화상자에 타이틀을 사용하지 않음
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityThemaBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        val themaUtil = ThemaUtil()
        binding.themaSetting.setOnCheckedChangeListener { _, checkedId ->
            Log.d(TAG, "radioThemaSetting: $checkedId")
            when (checkedId) {
                binding.lightModeRadio.id -> {
                    themaUtil.applyTheme("light")
                }
                binding.darkModeRadio.id -> {
                    themaUtil.applyTheme("dark")
                }
                binding.defaultModeRadio.id -> {
                    themaUtil.applyTheme("default")
                }
                else -> {
                    themaUtil.applyTheme("default")
                    throw IllegalArgumentException("error")
                }
            }
        }
    }
}