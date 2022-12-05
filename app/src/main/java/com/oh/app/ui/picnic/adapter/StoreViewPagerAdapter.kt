package com.oh.app.ui.picnic.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oh.app.R
import com.oh.app.data.store.Row

class StoreViewPagerAdapter(storeList: List<Row>) :
    RecyclerView.Adapter<StoreViewPagerAdapter.PagerViewHolder>() {
    var item = storeList

    override fun getItemCount(): Int = item.size
    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.store.text = item[position].UPSO_NM
        holder.menu.text = item[position].BIZCND_CODE_NM
        holder.address.text = item[position].RDN_CODE_NM
        Log.d("로그", "onBindViewHolder: ${item[position]}")
    }

    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val store = itemView.findViewById<TextView>(R.id.storeTextView)
        val menu = itemView.findViewById<TextView>(R.id.menuTextView)
        val address = itemView.findViewById<TextView>(R.id.addressTextview)
    }

    override fun onCreateViewHolder(view: ViewGroup, viewType: Int): PagerViewHolder {
        val view = LayoutInflater.from(view.context).inflate(R.layout.item_store, view, false)
        return PagerViewHolder(view)
    }
}