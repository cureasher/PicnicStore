package com.oh.app.ui.picnic.mart.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oh.app.data.mart.MartData
import com.oh.app.databinding.ListItemMartBinding
import com.oh.app.ui.picnic.chart.ChartActivity
import com.oh.app.ui.picnic.mart.MartActivity

class MartRecyclerAdapter(
    private var martList: MartData,
    private val owner: MartActivity
) :
    RecyclerView.Adapter<MartViewHolder>() {
    private lateinit var binding: ListItemMartBinding
    private var list = martList.martData.martInfoList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MartViewHolder {
        binding = ListItemMartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MartViewHolder, position: Int) {
        val martItem = list[position]
        with(holder.binding) {
            // 상품 이름, 개수, 가격, 날짜
            productName.text = martItem.aName
            productCount.text = martItem.AUnit
            productPrice.text = martItem.aPrice
            productDate.text = martItem.pYearMonth
        }
        holder.itemView.setOnClickListener {
            val chartIntent = Intent(owner, ChartActivity::class.java)
            chartIntent.putExtra("data1", martList)
            chartIntent.putExtra("name", martItem.aName)

            owner.startActivity(chartIntent)
        }
    }

    override fun getItemCount() = list.size
    fun refreshMartItems() {
        notifyItemChanged(0, list.size)
    }
}