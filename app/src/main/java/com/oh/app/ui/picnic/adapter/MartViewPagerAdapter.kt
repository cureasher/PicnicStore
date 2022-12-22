package com.oh.app.ui.picnic.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.view.clicks
import com.oh.app.common.MartInit
import com.oh.app.common.TAG
import com.oh.app.common.toastMessage
import com.oh.app.data.mart.MartRow
import com.oh.app.databinding.ItemMartBinding
import com.oh.app.ui.picnic.PicnicFragment
import com.oh.app.ui.picnic.mart.MartActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * 마트 뷰페이저용 어댑터
 */
class MartViewPagerAdapter(private val owner: PicnicFragment, private val guName: String) :
    RecyclerView.Adapter<MartViewPagerAdapter.PagerViewHolder>() {
    private lateinit var binding: ItemMartBinding
    var martList = ArrayList<MartRow>()

    init {
        martList = MartInit.martList
    }

    fun martListSet(list: ArrayList<MartRow>) {
        martList = list
    }

    override fun getItemCount() = martList.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.store.text = martList[position].martName
        holder.menu.text = martList[position].martType
        Log.d(TAG, "뷰페이저 데이터 확인: ${martList[position].martName}")
        holder.martInfo.setOnClickListener {
            val martIntent = Intent(owner.requireContext(), MartActivity::class.java)
            martIntent.putExtra("name", martList[position].martName)
            martIntent.putExtra("guName", guName)
            owner.startActivity(martIntent)
        }
        holder.move.setOnClickListener {
            deployListener()
        }
    }

    inner class PagerViewHolder(val binding: ItemMartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val store = binding.storeTextView
        val menu = binding.menuTextView
        val martInfo = binding.martInfo
        val move = binding.moveIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        binding = ItemMartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagerViewHolder(binding)
    }

    // RxBinding
    private val compositeDisposable = CompositeDisposable()

    // 플로우에 사용할 변수
//    private val defaultScope = CoroutineScope(Dispatchers.Default)

    /**
     * 이중 클릭 방지 리스너
     */
    private fun deployListener() {
        /**
         * Kotlin Code
         */
/*        var buttonClick = 1
        val btn1Label = "코드로 이중 클릭 방지 클릭횟수 : %d"
        with(binding) {
            moveIcon.onAvoidDuplicateClick(3000) {
                toastMessage(String.format(btn1Label, buttonClick++))
            }
        }*/
        /**
         * RxBinding throttleFirst
         */
        var rxThrottleButtonClick = 1
        val btn2Label = "Rx(Throttle) Binding 클릭횟수 : %d"
        compositeDisposable.add(
            binding.moveIcon
                .clicks()
                .observeOn(Schedulers.io())
                .throttleFirst(3000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    toastMessage(String.format(btn2Label, rxThrottleButtonClick++))
                }, {
                    Log.e("RX_ERROR", it.toString())
                })
        )

        /**
         * 플로우 중복클릭 방지
         */
        /*val btn4Label = "Flow(Throttle) 클릭횟수 : %d"
        var flowThrottleButtonClick = 1
        binding.moveIcon.setCoroutineFlowClickAction(3000, defaultScope) {
            CoroutineScope(Dispatchers.Main).launch {
                toastMessage(String.format(btn4Label, flowThrottleButtonClick++))
            }
        }*/
    }

    fun refreshMartItems() {
        notifyItemChanged(0, martList.size)
    }
}
