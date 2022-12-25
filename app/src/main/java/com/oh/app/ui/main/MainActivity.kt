package com.oh.app.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.oh.app.R
import com.oh.app.databinding.ActivityMainBinding
import com.oh.app.ui.picnic.PicnicFragment
import com.oh.app.ui.recipe.RecipeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val picnicFragment = PicnicFragment()
        val recipeFragment = RecipeFragment()

        if (savedInstanceState == null) {
            val ft = supportFragmentManager.beginTransaction()
            val fragment1 = PicnicFragment.newInstance("Owner Activity 에서 넘어온 값")
            ft.add(R.id.tabContent, fragment1)
            ft.commit()
        }
        with(binding) {
            // 탭레이아웃 이벤트 리스너
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val ft = supportFragmentManager.beginTransaction()
                    var position = tab.position
                    var selected: Fragment? = picnicFragment
                    when (position) {
                        0 -> {
                            selected = picnicFragment
                        }
                        1 -> {
                            selected = recipeFragment
                        }
                    }
                    with(ft) {
                        replace(R.id.tabContent, selected!!)
                        commit()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }
}