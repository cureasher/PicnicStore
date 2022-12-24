@file:Suppress("DEPRECATION")

package com.oh.app.ui.picnic.chart

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.oh.app.common.TAG
import com.oh.app.data.mart.MartData
import com.oh.app.data.mart.Row
import com.oh.app.databinding.ActivityChartBinding
import java.time.LocalDate
import java.util.*

/**
 * 물가정보 차트 보여주는 Activity
 */
class ChartActivity : AppCompatActivity() {
    lateinit var binding: ActivityChartBinding
    private var priceMap = mutableMapOf<Float, Int>()
    private var appleList :List<Row>? = listOf()
    var name = ""

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val martPriceData = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("data1", MartData::class.java)
        } else {
            intent.getParcelableExtra("data1")
        }

        name = intent.getStringExtra("name").toString()

        // 상품 데이터 리스트
        appleList = martPriceData?.martData?.martInfoList?.filter { it.aName == name }?.toList()
        val currentYear = LocalDate.now()
        val lastYear = yearChangeSplit(currentYear.toString())-1

        Log.d(TAG, "year 값: ${yearChangeSplit(currentYear.toString())}")
        Log.d(TAG, "onCreate: $currentYear, $lastYear")
        val monthList = appleList?.asSequence()?.filter {
            it.pYearMonth.substring(0, 4) == "2022"
                    || it.pYearMonth.substring(0, 4) == "2023"
        }?.toList()
            ?.distinctBy { it.pYearMonth }?.toList()?.sortedBy { it.pYearMonth }?.toList()
        Log.d(TAG, "로그 확인 ${monthList?.takeLast(12)}")
        Log.d(TAG, "로그 확인 ${Calendar.MONTH}")

        // 마트 데이터가 올해가 몇월인지 파악하고 올해가 1월이면 2022년 1월~12월 정보만 가져옴

        Log.d(TAG, "월 데이터: ${appleList?.map { it.pYearMonth }?.toSet()?.take(12)?.toSortedSet()}")
        // 맵에다 원하는 값 넣어주면 되는 것 가타. 맵에 가격, 날짜
        // 월별 가격데이터를 차트데이터 넣어주기
        var month = 1
        // 로우데이터를 받아 놓는거
        monthList?.forEach {
            Log.d(TAG, "${it.pYearMonth}, ${it.aPrice}원")
            priceMap[month++.toFloat()] = it.aPrice.toInt()
        }

        initBarChart(binding.barChart)
        setData(binding.barChart)
    }

    private fun initBarChart(barChart: BarChart) {
        with(barChart){
            // 차트 회색 배경 설정 (default = false)
            setDrawGridBackground(false)
            // 막대 그림자 설정 (default = false)
            setDrawBarShadow(false)
            // 차트 테두리 설정 (default = false)
            setDrawBorders(false)
            val description = Description()
            // 오른쪽 하단 모서리 설명 레이블 텍스트 표시 (default = false)
            description.isEnabled = false
            this.description = description
            // X, Y 바의 애니메이션 효과
            animateY(1000)
            animateX(1000)

            // 바텀 좌표 값
            val xAxis: XAxis = this.xAxis
            with(xAxis){
                labelCount = 12
                // x축 위치 설정
                position = XAxis.XAxisPosition.BOTTOM
                // 그리드 선 수평 거리 설정
                granularity = 1f
                // x축 텍스트 컬러 설정
                textColor = Color.BLACK
                // x축 선 설정 (default = true)
                setDrawAxisLine(false)
                // 격자선 설정 (default = true)
                setDrawGridLines(false)
            }

            val leftAxis: YAxis = this.axisLeft
            with(leftAxis){
                // 좌측 선 설정 (default = true)
                setDrawAxisLine(false)
                // 좌측 텍스트 컬러 설정
                textColor = Color.BLACK
            }

            val rightAxis: YAxis = this.axisRight
            rightAxis.isEnabled = false

            // 바차트의 타이틀
            val legend: Legend = this.legend
            with(legend){
                // 범례 모양 설정 (default = 정사각형)
                form = Legend.LegendForm.LINE
                // 타이틀 텍스트 사이즈 설정
                textSize = 20f
                // 타이틀 텍스트 컬러 설정
                textColor = Color.BLACK
                // 범례 위치 설정
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                // 범례 방향 설정
                orientation = Legend.LegendOrientation.HORIZONTAL
                // 차트 내부 범례 위치하게 함 (default = false)
                setDrawInside(false)
            }
        }
    }

    // 데이터 세팅
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setData(barChart: BarChart) {
        // Zoom In / Out 가능 여부 설정
        barChart.setScaleEnabled(false)
        val valueList = ArrayList<BarEntry>()

        // 물가 데이터
        priceMap.forEach {
            if (it.key.toInt() in 1..12) {
                valueList.add(BarEntry(it.key, it.value.toFloat()))
            }
        }
        Log.d(TAG, "가격 데이터: $priceMap")

        val maxPrice = priceMap.maxBy { it.value }.value
        val maxMonth =
            priceMap.filter { it.value == maxPrice }.keys.toList().map { it.toInt() }.toList()

        val minPrice = priceMap.minBy { it.value }.value
        val minMonth =
            priceMap.filter { it.value == minPrice }.keys.toList().map { it.toInt() }.toList()

        binding.maxMonthTV.text = "올해 가장 비싼 달은 ${listToString(maxMonth)}월"
        binding.maxPriceTV.text = "가격은 $maxPrice 원"

        binding.minMonthTV.text = "올해 가장 싼 달은 ${listToString(minMonth)}월"
        binding.minPriceTV.text = "가격은 $minPrice 원"

        val barDataSet = BarDataSet(valueList, name)
        Log.d(TAG, "name:$name ")
        // 바 색상 설정 (ColorTemplate.LIBERTY_COLORS)
        barDataSet.setColors(Color.rgb(118, 174, 175))

        val monthList: List<String> = List(13) { i -> i }.map { it.toString() + "월" }.toList()
        Log.d(TAG, "setData: $monthList")
        val monthArrayList = ArrayList<String>()
        monthArrayList.addAll(monthList)


        val data = BarData(barDataSet)
        barChart.data = data
        barChart.xAxis.valueFormatter = MyValueFormatter(monthArrayList)
        barChart.invalidate()
    }

    /**
     * 문자열 연도로 바꿔주는 함수
     */
    private fun yearChangeSplit(year: String): Int {
        return year.split("-")[0].toInt() + year.split("-")[1].toInt()
    }

    private fun listToString(list: List<Int>): String {
        return when (list.size) {
            1 -> list[0].toString()
            else -> list[0].toString()
        }
    }

    // 차트 밸류 포매터를 재정의해줘야함.
    private inner class MyValueFormatter(private val dateList: ArrayList<String>) :
        ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return dateList.getOrNull(value.toInt()) ?: value.toString()
        }
    }
}