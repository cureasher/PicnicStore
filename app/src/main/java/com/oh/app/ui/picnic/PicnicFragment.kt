@file:Suppress("DEPRECATION")

package com.oh.app.ui.picnic

import StoreViewModelFactory
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.oh.app.R
import com.oh.app.common.MartInit
import com.oh.app.common.PicnicStoreApplication
import com.oh.app.common.toastMessage
import com.oh.app.data.store.Row
import com.oh.app.databinding.PicnicFragmentBinding
import com.oh.app.ui.base.BaseFragment
import com.oh.app.ui.picnic.adapter.StoreViewPagerAdapter
import com.oh.app.ui.picnic.repository.StoreRepository
import com.oh.app.ui.picnic.repository.StoreRetrofitService
import net.daum.mf.map.api.*
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.MutableMap
import kotlin.collections.emptyList
import kotlin.collections.forEach
import kotlin.collections.indexOf
import kotlin.collections.isNotEmpty
import kotlin.collections.map
import kotlin.collections.set

const val LBS_CHECK_TAG = "LBS_CHECK_TAG"
const val LBS_CHECK_CODE = 100
var storeList: List<Row> = emptyList()
var markerResolver: MutableMap<Row, MapPOIItem> = HashMap()

class PicnicFragment : BaseFragment<PicnicFragmentBinding>(), MapView.MapViewEventListener,
    StoreViewPagerAdapter.OnStoreInterface {
    private lateinit var mapView: MapView
    private lateinit var mapPOIItem: MapPOIItem
    private lateinit var eventListener: MarkerEventListener
    private lateinit var storeAdapter: StoreViewPagerAdapter
    private lateinit var storeViewPager: ViewPager2
    private lateinit var data: Row
    private lateinit var spinner: Spinner
    private lateinit var guInfo: String
    private lateinit var storeViewModel: StoreViewModel
    private var isCheckd = true
    var fabVisible = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
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
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storeViewPager = binding.storeViewPager
        eventListener = MarkerEventListener(requireContext())
        mapView = MapView(context)
        mapView.setPOIItemEventListener(eventListener)
        mapView.setMapViewEventListener(this)


        // 초기 체크 유무 true
        with(binding) {
            martChip.isChecked = true
            restaurantChip.isChecked = true
        }

        binding.mapView.addView(mapView)
        if (isNetworkAvailable()) { // 현재 단말기의 네트워크 가능여부를 알아내고 시작한다
            checkLocationCurrentDevice()
        } else {
            Log.e(LBS_CHECK_TAG, "네트웍 연결되지 않음!")
            toastMessage("네트웍이 연결되지 않아 종료합니다.")
        }

        spinnerSetting()

        guInfo = PicnicStoreApplication.pref.getString("guInfo", "")
        spinner.setSelection(getIndex(spinner, guInfo))
        chipSetting()
        fabSetting(binding)
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = cm.activeNetwork ?: return false

            val networkCapabilities = cm.getNetworkCapabilities(nw) ?: return false
            return when {
                // 현재 단말기의 연결유무(wifi, data 통신)
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // 단말기가 아닐경우 (ex IoT 장비 등)
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                // 블루투스 인터넷 연결유무
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            return cm.activeNetworkInfo?.isConnected ?: false
        }
    }

    private fun checkLocationCurrentDevice() {
//        val locationIntervalTime = 3000L
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
                /**
                 * 정확한 위치를 기다림 : true 일시
                 * 지하, 이동중일 경우 늦어질 수 있음
                 */
                .setWaitForAccurateLocation(true).build()
//                .setMinUpdateIntervalMillis(locationIntervalTime) // 위치 획득 후 update 되는 최소 주기
//                .setMaxUpdateDelayMillis(locationIntervalTime).build() // 위치 획득 후 update delay 최대
        val lbsSettingsRequest: LocationSettingsRequest =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val settingClient: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val taskBSSettingResponse: Task<LocationSettingsResponse> =
            settingClient.checkLocationSettings(lbsSettingsRequest)
        // 위치정보 설정이 on일 경우
        taskBSSettingResponse.addOnSuccessListener {
            with(mapView) {
                currentLocationTrackingMode =
                    MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
                createDefaultMarker(mapView)
            }
        }

        // 위치 정보 설정이 off일 경우
        taskBSSettingResponse.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    toastMessage("위치정보 설정은 반드시 On 상태여야 해요!")
                    exception.startResolutionForResult(requireActivity(), LBS_CHECK_CODE)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(LBS_CHECK_TAG, sendEx.message.toString())
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == LBS_CHECK_CODE) {
            checkLocationCurrentDevice()
        }
    }


    // 현재 위치 찍기
    @SuppressLint("MissingPermission")
    private fun createDefaultMarker(mapView: MapView) {
        var mDefaultMarker = MapPOIItem()
        val name = "현재 위치"

        MartInit.fusedMyLocation()

        // 프리퍼런스에 저장
        var latitude = PicnicStoreApplication.pref.getString("latitude", "nolatitude")
        var longitude = PicnicStoreApplication.pref.getString("longitude", "nolongitude")

        Log.d("로그", "구정보 위치 확인: ${latitude}, ${longitude}")
        with(mDefaultMarker) {
            itemName = name
            tag = 0
            mapPoint = MapPoint.mapPointWithGeoCoord(latitude.toDouble(), longitude.toDouble())

            markerType = MapPOIItem.MarkerType.BluePin
            selectedMarkerType = MapPOIItem.MarkerType.RedPin
            with(mapView) {
                addPOIItem(mDefaultMarker)
                selectPOIItem(mDefaultMarker, false)
                setMapCenterPointAndZoomLevel(mapPoint, 2, false)
            }
        }
    }

    // 식당 마커 찍기
    @SuppressLint("MissingPermission", "SuspiciousIndentation")
    private fun storeMarker(storeInfo: Row): MapPOIItem {
        var mStoreMarker = MapPOIItem()
        with(mStoreMarker) {
            itemName = storeInfo.UPSO_NM
            tag = 2
            mapPoint = MapPoint.mapPointWithGeoCoord(
                storeInfo.Y_DNTS.toDouble(),
                storeInfo.X_CNTS.toDouble()
            )
            markerType = MapPOIItem.MarkerType.CustomImage
            selectedMarkerType = MapPOIItem.MarkerType.CustomImage
            customImageResourceId = R.drawable.rice_mart_icon
            customSelectedImageResourceId = R.drawable.rice_mart_select
            with(mapView) {
                addPOIItem(mStoreMarker)
                selectPOIItem(mStoreMarker, true)
                setZoomLevel(3, true)
            }
            val bounds = MapPointBounds(mapPoint, mapPoint)
//            mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds))
        }
        return mStoreMarker
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun martMapSetting(guInfo: String) {
        val martPoiInfo = MartInit.setMartPoiList()
        val data = MartInit.setMartPoiList()
        Log.d("로그", "마트이름: ${MartInit.getMart(guInfo)} ")
        val martNameList = MartInit.getMart(guInfo)

        martPoiInfo?.forEach { storeName, _ ->
            martNameList!!.forEach {
                if (storeName == it) {
//                    Log.d("로그", "storeObserverSetup: $s, ${mapPoint}")
                    with(mapView) {
                        mapType = MapView.MapType.Standard
                        setZoomLevel(3, true)
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
                        }
                    }
                }
            }
        }
//        displayPOI(storeList)
        Log.d("로그", "martMapSetting: ${storeList.size} storelist 값 확인")
//        val guInfo = PicnicStoreApplication.pref.getString("guInfo", "noguInfo")

        Log.d("로그", "martMapSetting: ${MartInit.getMart(guInfo)} ")
    }

    // 맵 초기 세팅
//    private fun mapSetting(data: Row) {
//        with(mapView) {
//            mapType = MapView.MapType.Standard
//            setZoomLevel(3, true)
//            zoomIn(true)
//            zoomOut(true)
//            addPOIItem(storeMarker(data)) // 행사위치 핑
//        }
//    }

    // 뷰페이저 클릭시 마커되는 부분
    private fun addMapPoiMarker(data: Row): MapPOIItem {
        val marker = MapPOIItem()
        with(marker) {
            tag = 2
            markerType = MapPOIItem.MarkerType.CustomImage // 마커 색
            selectedMarkerType = MapPOIItem.MarkerType.CustomImage
            customImageResourceId = R.drawable.rice_mart_icon
            customSelectedImageResourceId = R.drawable.rice_mart_select
            mapPoint = MapPoint.mapPointWithGeoCoord(
                data.Y_DNTS.toDouble(),
                data.X_CNTS.toDouble()
            ) // poi장소 좌표
            itemName = data.UPSO_NM // 장소명
        }
        markerResolver[data] = marker
        return marker
    }

    // 전체 마커 map 표시 함수 식당 마커 찍기
    @RequiresApi(Build.VERSION_CODES.N)
    private fun displayPOI(data: List<Row>) {

        with(mapView) {
            removeAllPOIItems() // 기존 마커들 제거
            for (i in 0 until data.size) {
                addPOIItem(addMapPoiMarker(data[i])) // 현 마커 추가
                addPOIItem(storeMarker(data[i])) // 행사위치 핑
            }
            // 마트 마커 찍기
            martMapSetting(data[0].CGG_CODE_NM)

        }
    }

    // 마커 클릭 이벤트
    inner class MarkerEventListener(val context: Context) : MapView.POIItemEventListener {
        override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
            Log.d("로그", "onPOIItemSelected: ${poiItem?.itemName} ")
//            mapView?.setMapCenterPoint(poiItem?.mapPoint, false)
            binding.storeViewPager.visibility = View.VISIBLE
            binding.mapOverlay.visibility = View.VISIBLE

            // 마커 인덱스 값 확인
            Log.d(
                "로그",
                "마커의 인덱스 값: ${storeList.map { row -> row.UPSO_NM }.indexOf(poiItem?.itemName)}"
            )
            val markerIndex = storeList.map { row -> row.UPSO_NM }.indexOf(poiItem?.itemName)
            with(binding.storeViewPager) {
                if (markerIndex >= -1) {
                    storeViewPager.setCurrentItem(markerIndex, false)
                } else {
                    storeViewPager.setCurrentItem(storeList.size, false)
                }
            }
        }

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
            mapView: MapView?,
            poiItem: MapPOIItem?,
            mapPoint: MapPoint?
        ) {
            // 마커의 속성 중 isDraggable = true 일 때 마커를 이동시켰을 경우
        }
    }

    override fun onMapViewInitialized(p0: MapView?) {
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
    }

    override fun onMapViewSingleTapped(mapView: MapView?, mapPoint: MapPoint?) {
        binding.storeViewPager.visibility = View.INVISIBLE
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

    override fun onItemClick(pos: Int) {
        Log.d("로그", "onItemClick: 클릭함")
        val marker = markerResolver[storeList[pos]]
        if (binding.storeViewPager.visibility == View.VISIBLE) {
            // 해당 위치로 지도 중심점 이동, 지도 확대
            if (marker != null) {
                val update = CameraUpdateFactory.newMapPoint(marker.mapPoint, 3F)
                with(mapView) {
                    animateCamera(
                        update,
                        object : net.daum.mf.map.api.CancelableCallback {
                            override fun onFinish() {
                                selectPOIItem(marker, true) // 선택한 상점 마커 선택
                            }

                            override fun onCancel() {

                            }
                        })
                }
            }
        }
    }


    fun spinnerSetting() {
        spinner = binding.locationSpinner
        val locationAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.location_array,
            R.layout.spinner_item
        )
        locationAdapter.setDropDownViewResource(R.layout.spinner_drop_down)
        with(spinner) {
            adapter = locationAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    val gu = spinner.getItemAtPosition(pos)
                    Log.d("로그", "스피너 onItemSelected: ${spinner.getItemAtPosition(pos)} ")
                    storeViewModelSetting(gu.toString())
//                    storeViewModel.getStoreViewModel(guInfo)
//                    storeViewModel.guValue.value = parent?.getItemAtPosition(pos).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    parent?.getItemAtPosition(getIndex(binding.locationSpinner, "금천구"))
                    Log.d("로그", "onNothingSelected: 스피넛 실패")
                }
            }
        }
    }

    fun getIndex(spinner: Spinner, item: String): Int {
        for (i in 0..spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == item) {
                Log.d("로그", "getIndex: $i $item")
                return i
            }
        }
        return 0
    }

    @SuppressLint("RestrictedApi")
    fun chipSetting() {
        with(binding) {
            if (isCheckd) {
                martChip.setTextColor(Color.WHITE)
                restaurantChip.setTextColor(Color.WHITE)
            }
            martChip.setInternalOnCheckedChangeListener { checkable, isChecked ->
                if (isChecked) {
                    martChip.chipBackgroundColor =
                        ContextCompat.getColorStateList(requireContext(), R.color.dark_blue)
                    martChip.setTextColor(Color.WHITE)
                } else {
                    martChip.chipBackgroundColor =
                        ContextCompat.getColorStateList(requireContext(), R.color.light_blue)
                    martChip.setTextColor(Color.BLACK)
                }
            }
            restaurantChip.setInternalOnCheckedChangeListener { checkable, isChecked ->
                if (isChecked) {
                    restaurantChip.chipBackgroundColor =
                        ContextCompat.getColorStateList(requireContext(), R.color.dark_blue)
                    restaurantChip.setTextColor(Color.WHITE)
                } else {
                    restaurantChip.chipBackgroundColor =
                        ContextCompat.getColorStateList(requireContext(), R.color.light_blue)
                    restaurantChip.setTextColor(Color.BLACK)
                }
            }
        }
    }

    fun fabSetting(binding: PicnicFragmentBinding) {
        binding.moreFab.setOnClickListener {
            if (!fabVisible) {
                with(binding) {
                    licenceFab.show()
                    priceListFab.show()
                    darkModeFab.show()

                    licenceFab.visibility = View.VISIBLE
                    priceListFab.visibility = View.VISIBLE
                    darkModeFab.visibility = View.VISIBLE

                    moreFab.setImageDrawable(resources.getDrawable(com.google.android.material.R.drawable.ic_m3_chip_close))

                    fabVisible = true
                }
            } else {
                with(binding) {
                    licenceFab.hide()
                    priceListFab.hide()
                    darkModeFab.hide()

                    licenceFab.visibility = View.GONE
                    priceListFab.visibility = View.GONE
                    darkModeFab.visibility = View.GONE
                    darkModeFab.setImageDrawable(resources.getDrawable(R.drawable.moon))
                    fabVisible = false
                    moreFab.setImageDrawable(resources.getDrawable(R.drawable.more_icon))
                }
            }

            binding.licenceFab.setOnClickListener {
//                OssLicensesMenuActivity.setActivityTitle("오픈소스 라이센스 목록")
//                startActivity(Intent(activity, OssLicensesActivity::class.java))
            }

            binding.priceListFab.setOnClickListener {

            }

            binding.darkModeFab.setOnClickListener {

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun storeViewModelSetting(guInfo: String) {
        val storeViewModel: StoreViewModel =
            ViewModelProvider(
                this, StoreViewModelFactory(
                    StoreRepository(StoreRetrofitService.getInstance())
                )
            ).get(StoreViewModel::class.java)
        storeViewModel.storeList.observe(viewLifecycleOwner) {
            storeList = it.StoreInfo.row

            Log.d("로그", "storeObserverSetup: ${it.StoreInfo.row}")
            with(storeViewPager) {
                run {
                    displayPOI(storeList)
                    storeAdapter = StoreViewPagerAdapter(it.StoreInfo.row, this@PicnicFragment)
//                    storeAdapter.setItemClickListener(object :
//                        StoreViewPagerAdapter.OnItemClickListener {
//                        override fun onItemClick(pos: Int) {
//                            Log.d("로그", "onItemClick: $pos")
//                            toastMessage(pos.toString())
//                        }
//                    })
                    orientation = ViewPager2.ORIENTATION_HORIZONTAL

                    storeList.forEachIndexed { index, _ ->
                        mapPOIItem = storeMarker(storeList[index])
                        mapView.addPOIItem(mapPOIItem)
                        mapView.selectPOIItem(mapPOIItem, false)
                        adapter = storeAdapter
                        data = it.StoreInfo.row[index]
                    }
                }
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)

//                        val selectedStoreModel= storeAdapter.currentList[position]
                        if (storeList.isNotEmpty()) {
                            val marker = markerResolver[storeList[position]]
                            if (binding.storeViewPager.visibility == View.VISIBLE) {
                                // 해당 위치로 지도 중심점 이동, 지도 확대
                                if (marker != null) {
                                    val update =
                                        CameraUpdateFactory.newMapPoint(marker.mapPoint, 3F)
                                    with(mapView) {
                                        animateCamera(
                                            update,
                                            object : net.daum.mf.map.api.CancelableCallback {
                                                override fun onFinish() {
                                                    selectPOIItem(marker, true) // 선택한 상점 마커 선택
                                                }

                                                override fun onCancel() {

                                                }
                                            })
                                    }
                                }
                            }

                        }
                    }
                })
            }
        }
        binding.progressBar.visibility = View.GONE // 로딩

        storeViewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            Log.e("로그", it)
        }
        storeViewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })
        storeViewModel.getStoreViewModel(guInfo)
    }
}


