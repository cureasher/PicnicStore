package com.oh.app.ui.picnic

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.chip.Chip
import com.oh.app.R
import com.oh.app.common.*
import com.oh.app.data.mart.MartRow
import com.oh.app.data.store.Row
import com.oh.app.databinding.PicnicFragmentBinding
import com.oh.app.rest.setting.RetrofitGenerator
import com.oh.app.ui.base.BaseFragment
import com.oh.app.ui.main.MainActivity
import com.oh.app.ui.oss.OpenSourceLicenseActivity
import com.oh.app.ui.picnic.adapter.MartViewPagerAdapter
import com.oh.app.ui.picnic.adapter.StoreViewPagerAdapter
import com.oh.app.ui.picnic.store.StoreViewModel
import com.oh.app.ui.picnic.store.StoreViewModelFactory
import com.oh.app.ui.picnic.store.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.daum.mf.map.api.*
import kotlin.collections.set

var markerResolver: MutableMap<Row, MapPOIItem> = HashMap()

@Suppress("DEPRECATION")
class PicnicFragment : BaseFragment<PicnicFragmentBinding>(), MapView.MapViewEventListener {
    private val locationMap: HashMap<String, Double> = HashMap()
    private var latitude = locationMap["lati"] ?: 0.0
    private var longitude = locationMap["longi"] ?: 0.0
    private var currentAreaName = ""
    private lateinit var mapView: MapView
    private lateinit var eventListener: MarkerEventListener
    private lateinit var storeAdapter: StoreViewPagerAdapter
    private lateinit var martAdapter: MartViewPagerAdapter
    private lateinit var storeViewPager: ViewPager2
    private lateinit var martViewPager: ViewPager2
    private lateinit var data: Row
    private lateinit var spinner: Spinner
    private lateinit var guInfo: String
    private var fabVisible = false
    private var selectChip: String = "음식점"
    private lateinit var client: FusedLocationProviderClient
    private var martList: ArrayList<MartRow> = arrayListOf()

    override fun getFragmentBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): PicnicFragmentBinding {
        return PicnicFragmentBinding.inflate(inflater, container, false)
    }

    companion object {
        fun newInstance(param: String): Fragment {
            val fragment: Fragment = PicnicFragment()
            val bundle = Bundle()
            bundle.putString("title", param)
            fragment.arguments = bundle
            return fragment
        }
        var storeList: List<Row> = emptyList()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        picnicBindingInit()
        markerEventInit()
        picnicChipInit()
        kakaoMapInit()
        getCurrentLocation()
        locationCheckNetwork()
        addMartViewpager()
        spinnerSetting()
        fabSetting()
        chipSetting()
        addGuInfoShared()
    }

    private fun picnicBindingInit() {
        binding.picnicChip.isSingleSelection
        storeViewPager = binding.storeViewPager
        martViewPager = binding.martViewPager
    }

    private fun markerEventInit() {
        // 마커이벤트 등록
        mapView = MapView(context)
        eventListener = MarkerEventListener(requireContext())

        // 마커 포이 이벤트
        mapView.setPOIItemEventListener(eventListener)
        // 맵뷰 이벤트
        mapView.setMapViewEventListener(this)
    }

    private fun picnicChipInit() {
        with(binding) {
            // 초기 chip 체크
            martChip.isChecked = true
            restaurantChip.isChecked = false
        }
    }

    private fun kakaoMapInit() {
        // 카카오 맵뷰 등록
        binding.mapView.addView(mapView)
    }

    private fun locationCheckNetwork() {
        // 로케이션 불렀을 때 네트워크 체크
        binding.myLocationFab.setOnClickListener {
            // 현재 단말기의 네트워크 가능여부를 알아내고 시작한다 -> location click
            if (isNetworkAvailable()) {
                checkLocationCurrentDevice()
            } else {
                Log.e(TAG, "네트웍 연결되지 않음!")
                toastMessage("네트웍이 연결되지 않아 종료합니다.")
            }
        }
    }

    private fun addMartViewpager() {
        // 마트뷰페이저 등록
        with(binding.martViewPager) {
            guInfo = "금천구"
            val martAdapter = MartViewPagerAdapter(this@PicnicFragment, guInfo)
            adapter = martAdapter
            martList = martAdapter.martList
        }
    }

    private fun addGuInfoShared() {
        // 쉐어드 프리퍼런스에서 값 불러오기 -> 현재 set 등록 안된 경우 디폴트로 금천구만 불러옴)
        guInfo = PicnicStoreApplication.pref.getString("guInfo", "금천구")
        spinner.setSelection(getIndex(spinner, guInfo))
    }

    // 네트워크 확인 함수
    private fun isNetworkAvailable(): Boolean {
        val cm = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = cm.activeNetwork ?: return false

        val networkCapabilities = cm.getNetworkCapabilities(nw) ?: return false
        return when {
            // 현재 단말기의 ©연결유무(wifi, data 통신)
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            // 단말기가 아닐경우 (ex IoT 장비 등)
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            // 블루투스 인터넷 연결유무
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        // location manager init
        val locationManager: LocationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        client = LocationServices.getFusedLocationProviderClient(requireActivity())
        // check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {
            client.lastLocation.addOnCompleteListener { task ->
                // Initialize location
                val location = task.result

                // Check condition
                if (location != null) {
                    locationMap["lati"] = location.latitude
                    locationMap["longi"] = location.longitude
                    Log.d(TAG, "1: ${locationMap["lati"]}, ${locationMap["longi"]}")
                }
            }
        }
    }

    private fun checkLocationCurrentDevice() {
        /**
         * 정확한 위치를 기다림 : true 일시
         * 지하, 이동중일 경우 늦어질 수 있음
         */
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
            .setWaitForAccurateLocation(true).build()

        val lbsSettingsRequest: LocationSettingsRequest =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val settingClient: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val taskBSSettingResponse: Task<LocationSettingsResponse> =
            settingClient.checkLocationSettings(lbsSettingsRequest)
        // 위치정보 설정이 on일 경우
        taskBSSettingResponse.addOnSuccessListener {
            toastMessage("위치정보 on 현재위치로 이동")
            // 현위치로 가는 코드 구현
            latitude = locationMap["lati"] ?: 0.0
            longitude = locationMap["longi"] ?: 0.0
            Log.d(TAG, "3: $latitude, $longitude")
            val currentLocation = LocationRepository(RetrofitGenerator.getKakaoInstance())

            CoroutineScope(Dispatchers.Main).launch {
                Dispatchers.Main.run {
                    val loc = currentLocation.getCurrentInfo(longitude, latitude)
                    currentAreaName = loc.body()?.documents?.get(0)?.address?.region2depthName!!
                    Log.d(
                        TAG, "위치 : ${loc.body()?.documents?.get(0)?.address?.region2depthName}"
                    )
                    spinner.setSelection(getIndex(spinner, currentAreaName))
                    mapView.setMapCenterPoint(
                        MapPoint.mapPointWithGeoCoord(latitude, longitude), true
                    )
                }
            }
        }

        // 위치 정보 설정이 off 일 경우
        taskBSSettingResponse.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    toastMessage("위치정보 설정은 반드시 On 상태여야 해요!")
                    exception.startResolutionForResult(requireActivity(), LBS_CHECK_CODE)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, sendEx.message.toString())
                }
            }
        }
    }

    // 마트 마커 찍기
    @RequiresApi(Build.VERSION_CODES.N)
    private fun martMapSetting(guInfo: String) {
        val martPoiInfo = MartInit.setMartPoiList()
        val data = MartInit.setMartPoiList()
        Log.d(TAG, "마트이름: ${MartInit.getMart(guInfo)} ")
        val martNameList = MartInit.getMart(guInfo)

        // 마트 포이 찍는 곳
        martPoiInfo.forEach { (storeName, _) ->
            martNameList!!.forEach {
                Log.d(TAG, "martMapSetting martNameList: $martNameList")
                Log.d(TAG, "martMapSetting martPoiInfo: $martPoiInfo")
                if (storeName == it) {
                    with(mapView) {
                        mapType = MapView.MapType.Standard
                        setZoomLevel(4, true)
                        zoomIn(true)
                        zoomOut(true)
                        val marker = MapPOIItem()
                        with(marker) {
                            itemName = storeName
                            tag = 2
                            mapPoint = data[storeName]
                            markerType = MapPOIItem.MarkerType.CustomImage
                            selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                            customImageResourceId = R.drawable.fruit_shop_icon
                            customSelectedImageResourceId = R.drawable.fruit_shop_select
                            mapView.addPOIItem(marker)
                            mapView.setMapCenterPoint(mapPoint, false)
                        }
                    }
                }
            }
        }
        Log.d(TAG, "martMapSetting: ${storeList.size} storeList 값 확인")
        Log.d(TAG, "martMapSetting: ${MartInit.getMart(guInfo)} ")

        martList[0] = MartRow(MartInit.getMart(guInfo)?.get(0) ?: "", "전통시장")
        martList[1] = MartRow(MartInit.getMart(guInfo)?.get(1) ?: "", "전통시장")
        martList[2] = MartRow(MartInit.getMart(guInfo)?.get(2) ?: "", "대형마트")
        martList[3] = MartRow(MartInit.getMart(guInfo)?.get(3) ?: "", "대형마트")

        Log.d(TAG, "martList: $martList ??")
        martAdapter = MartViewPagerAdapter(this@PicnicFragment, guInfo)
        martAdapter.martListSet(martList)
        martAdapter.refreshMartItems()
//        martUpdate()
    }

    // 식당 마커 추가 되는 부분
    private fun addStorePoiMarker(storeInfo: Row): MapPOIItem {
        val storeMarker = MapPOIItem()
        // 마커 설정
        with(storeMarker) {
            itemName = storeInfo.upsoNm
            tag = 2
            markerType = MapPOIItem.MarkerType.CustomImage
            selectedMarkerType = MapPOIItem.MarkerType.CustomImage
            customImageResourceId = R.drawable.rice_mart_icon
            customSelectedImageResourceId = R.drawable.rice_mart_select
            // 식당 좌표
            mapPoint = MapPoint.mapPointWithGeoCoord(
                storeInfo.longitude.toDouble(), storeInfo.latitude.toDouble()
            )
            mapView.setMapCenterPoint(mapPoint, true)
            mapView.setZoomLevel(4, false)
        }

        markerResolver[storeInfo] = storeMarker
        return storeMarker
    }

    // marker init
    private fun markerInit(data: List<Row>) {
        with(mapView) {
            removeAllPOIItems()
            currentLocation()
            for (element in data) {
                addPOIItem(addStorePoiMarker(element)) // 식당 마커 추가
            }
        }
    }

    // 전체 마커 map 표시 함수 식당 마커 찍기
    @RequiresApi(Build.VERSION_CODES.N)
    private fun displayPOI(data: List<Row>, chipInfo: String) {
        selectChip = chipInfo
        when (chipInfo) {
            "음식점" -> {
                with(mapView) {
                    removeAllPOIItems()
                    for (element in data) {
                        addPOIItem(addStorePoiMarker(element)) // 식당 마커 추가
                    }
                }
            }
            "마트" -> {
                with(mapView) {
                    removeAllPOIItems()
                    // 마트 마커 찍기(금천구 문자열 데이터만 필요)
                    martMapSetting(data[0].cggCodeNm)
                }
            }
        }
    }

    // 마커 클릭 이벤트
    inner class MarkerEventListener(val context: Context) : MapView.POIItemEventListener {
        override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
            Log.d(TAG, "onPOIItemSelected: ${poiItem?.itemName} ")
            when (selectChip) {
                "음식점" -> {
                    binding.storeViewPager.visibility = View.VISIBLE
                    binding.martViewPager.visibility = View.GONE
                }
                "마트" -> {
                    binding.martViewPager.visibility = View.VISIBLE
                    binding.storeViewPager.visibility = View.GONE
                }
            }
            binding.mapOverlay.visibility = View.VISIBLE

            // 마커 인덱스 값 확인
            Log.d(
                TAG,
                "마커의 인덱스 값 식당: ${storeList.map { row -> row.upsoNm }.indexOf(poiItem?.itemName)}"
            )
            Log.d(
                TAG,
                "마커의 인덱스 값 마트: ${martList.map { row -> row.martName }.indexOf(poiItem?.itemName)}"
            )

            val storeMarkerIndex = storeList.map { row -> row.upsoNm }.indexOf(poiItem?.itemName)
            val martMarkerIndex =
                martList.map { martInfo -> martInfo.martName }.indexOf(poiItem?.itemName)
            with(binding) {
                storeViewPager.also {
                    if (storeMarkerIndex >= -1) {
                        storeViewPager.setCurrentItem(storeMarkerIndex, false)
                    } else {
                        storeViewPager.setCurrentItem(storeList.size, false)
                    }
                }
                martViewPager.also {
                    if (martMarkerIndex >= -1) {
                        martViewPager.setCurrentItem(martMarkerIndex, false)
                    } else {
                        martViewPager.setCurrentItem(martList.size, false)
                    }
                }
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {
            // 말풍선 클릭시
        }


        override fun onCalloutBalloonOfPOIItemTouched(
            mapView: MapView?,
            poiItem: MapPOIItem?,
            mapPoIItem: MapPOIItem.CalloutBalloonButtonType?
        ) {
        }

        override fun onDraggablePOIItemMoved(
            mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?
        ) {
            // 마커의 속성 중 isDraggable = true 일 때 마커를 이동시켰을 경우
        }
    }

    override fun onMapViewInitialized(mapView: MapView?) {
    }

    override fun onMapViewCenterPointMoved(mapView: MapView?, mapPoint: MapPoint?) {
    }

    override fun onMapViewZoomLevelChanged(mapView: MapView?, mapPoint: Int) {
    }

    override fun onMapViewSingleTapped(mapView: MapView?, mapPoint: MapPoint?) {
        binding.storeViewPager.visibility = View.INVISIBLE
        binding.martViewPager.visibility = View.INVISIBLE
        binding.mapOverlay.visibility = View.INVISIBLE
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
    }

    // 스피너 세팅
    private fun spinnerSetting() {
        spinner = binding.locationSpinner
        val locationAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.location_array, R.layout.spinner_item
        )
        locationAdapter.setDropDownViewResource(R.layout.spinner_drop_down)
        with(spinner) {
            adapter = locationAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, pos: Int, id: Long
                ) {
                    val gu = spinner.getItemAtPosition(pos)
                    Log.d(TAG, "스피너 onItemSelected: ${spinner.getItemAtPosition(pos)} ")
                    storeViewModelSetting(gu.toString())
                    currentLocation()

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    parent?.getItemAtPosition(getIndex(binding.locationSpinner, "금천구"))
                    Log.d(TAG, "onNothingSelected: 스피너 실패")
                }
            }
        }
    }

    // 스피너 인덱스 불러오는 함수
    fun getIndex(spinner: Spinner, item: String): Int {
        for (i in 0..spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == item) {
                Log.d(TAG, "getIndex: $i $item")
                return i
            }
        }
        return 0
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun chipSetting() {
        Log.d(TAG, "chipSetting:  돌아가니?")
        with(binding) {
            picnicChip.isSingleSelection = true
            restaurantChip.isChecked = true
            Log.d(TAG, "chipSetting:  돌아가니?2")

            martChip.setTextColor(Color.BLACK)
            restaurantChip.setTextColor(Color.BLACK)

            picnicChip.setOnCheckedStateChangeListener { group, _ ->
                selectChip = group.children.toList().filter { (it as Chip).isChecked }
                    .joinToString(", ") { (it as Chip).text }
                Log.d(TAG, "chipSetting: $selectChip")
                displayPOI(storeList, selectChip)
                Log.d(TAG, "onViewCreated1: $selectChip")
                storeAdapter = StoreViewPagerAdapter(storeList)
                Log.d(TAG, "chipSetting:  돌아가니?3")
            }
            Log.d(TAG, "chipSetting: $storeList")
            Log.d(TAG, "chipSetting:  돌아가니?4")
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun fabSetting() {
        binding.moreFab.setOnClickListener {
            if (!fabVisible) {
                with(binding) {
                    settingFab.show()
                    expiryDateFab.show()

                    settingFab.visibility = View.VISIBLE
                    expiryDateFab.visibility = View.VISIBLE
//                    resources.getDrawable(R.drawable.more_icon)

//                    moreFab.setImageDrawable(resources.getDrawable(com.google.android.material.R.drawable.ic_m3_chip_close))
                    moreFab.setImageDrawable(resources.getDrawable(R.drawable.more_icon))
                    fabVisible = true
                }
            } else {
                with(binding) {
                    settingFab.hide()
                    expiryDateFab.hide()

                    settingFab.visibility = View.GONE
                    expiryDateFab.visibility = View.GONE
                    fabVisible = false
                    moreFab.setImageDrawable(resources.getDrawable(R.drawable.more_icon))
                }
            }

            binding.settingFab.setOnClickListener {
                val dialog = PicnicDialog(activity as MainActivity)
                dialog.show()
            }

            binding.expiryDateFab.setOnClickListener {
                startActivity(Intent(activity, OpenSourceLicenseActivity::class.java))
            }
        }
    }

    fun storeViewModelSetting(guInfo: String) {
        Log.d(TAG, "storeViewModelSetting1: $storeList")
        val storeViewModel: StoreViewModel = ViewModelProvider(
            this, StoreViewModelFactory(
                StoreRepository(RetrofitGenerator.getStoreInstance())
            )
        )[StoreViewModel::class.java]
        storeViewModel.storeList.observe(viewLifecycleOwner) {
            storeList = it.storeInfo.row
            markerInit(storeList)
            Log.d(TAG, "storeObserverSetup: ${it.storeInfo.row}")
            Log.d(TAG, "storeViewModelSetting: ${storeList.size}")
            with(storeViewPager) {
                run {
                    Log.d(TAG, "storeViewModelSetting: $selectChip")
                    storeAdapter = StoreViewPagerAdapter(storeList)
                    orientation = ViewPager2.ORIENTATION_HORIZONTAL
                }
                storeList.forEachIndexed { index, _ ->
                    adapter = storeAdapter
                    data = it.storeInfo.row[index]
                }
            }
        }
        Log.d(TAG, "storeViewModelSetting2: $storeList")
        binding.progressBar.visibility = View.GONE // 로딩

        storeViewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            Log.e(TAG, it)
        }
        storeViewModel.isLoading.observe(requireActivity()) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
        with(storeViewModel) {
            getStoreViewModel(guInfo)
        }
    }

    fun currentLocation() {
        val marker = MapPOIItem()
        marker.itemName = "현재위치"
        marker.tag = 0
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
        marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
        marker.selectedMarkerType =
            MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker)
        mapView.setMapCenterPoint(marker.mapPoint, false)
    }
}