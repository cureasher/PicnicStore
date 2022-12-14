@file:Suppress("DEPRECATION")

package com.oh.app.ui.picnic

import StoreViewModelFactory
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import com.oh.app.data.store.CrtfcUpsoInfo
import com.oh.app.data.store.Row
import com.oh.app.databinding.PicnicFragmentBinding
import com.oh.app.ui.base.BaseFragment
import com.oh.app.ui.picnic.adapter.StoreViewPagerAdapter
import com.oh.app.ui.picnic.repository.StoreRepository
import com.oh.app.ui.picnic.repository.StoreRetrofitService
import net.daum.mf.map.api.*
import net.daum.mf.map.api.MapView.CurrentLocationEventListener

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
        val storeViewModel: StoreViewModel = ViewModelProvider(
            this, StoreViewModelFactory(
                StoreRepository(StoreRetrofitService.getInstance())
            )
        ).get(StoreViewModel::class.java)
        storeObserverSetup(storeViewModel)
        storeViewModel.getStoreViewModel()
        eventListener = MarkerEventListener(requireContext())

        mapView = MapView(context)
        mapView.setPOIItemEventListener(eventListener)
        mapView.setMapViewEventListener(this)
        createDefaultMarker(mapView)

        binding.mapView.addView(mapView)
        if (isNetworkAvailable()) { // 현재 단말기의 네트워크 가능여부를 알아내고 시작한다
            checkLocationCurrentDevice()
        } else {
            Log.e(LBS_CHECK_TAG, "네트웍 연결되지 않음!")
            toastMessage("네트웍이 연결되지 않아 종료합니다.")
        }
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
        val locationIntervalTime = 3000L
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, locationIntervalTime)
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
            mapView.currentLocationTrackingMode =
                MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
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

    @SuppressLint("SuspiciousIndentation", "MissingPermission")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun storeObserverSetup(storeViewModel: StoreViewModel) {
        storeViewModel.storeList.observe(viewLifecycleOwner) {
            with(storeViewPager) {
                run {
                    mapSetting(it.StoreInfo.row[0])
                    val martPoiInfo = MartInit.setMartPoiList()
                    val guInfo = PicnicStoreApplication.pref.getString("guInfo", "noguInfo")
                    Log.d("로그", "마트이름: ${MartInit.getMart(guInfo)} ")
                    val martNameList = MartInit.getMart(guInfo)

                    martPoiInfo.forEach { s, mapPoint ->
                        martNameList?.forEach {
                            if (s == it) {
                                Log.d("로그", "storeObserverSetup: $s, ${mapPoint}")
                                martMapSetting(s, MartInit.setMartPoiList())
                            }
                        }
                    }
                    storeList = it.StoreInfo.row
                    storeAdapter = StoreViewPagerAdapter(it.StoreInfo.row,this@PicnicFragment)
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
            displayPOI(it.StoreInfo)
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
        storeViewModel.getStoreViewModel()
    }

    @SuppressLint("MissingPermission")
    private fun createDefaultMarker(mapView: MapView) {
        var mDefaultMarker = MapPOIItem()
        val name = "현재 위치"
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
                setMapCenterPointAndZoomLevel(mapPoint, 3, false)
            }
        }
    }

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
            mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds))
        }
        return mStoreMarker
    }

    private fun martMapSetting(storeName: String, data: HashMap<String, MapPoint>) {
        val guInfo = PicnicStoreApplication.pref.getString("guInfo", "noguInfo")

        Log.d("로그", "martMapSetting: ${MartInit.getMart(guInfo)} ")

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

    // 맵 초기 세팅
    private fun mapSetting(data: Row) {
        with(mapView) {
            mapType = MapView.MapType.Standard
            setZoomLevel(3, true)
            zoomIn(true)
            zoomOut(true)
            addPOIItem(storeMarker(data)) // 행사위치 핑
            Log.d(
                "로그",
                "GPS mapSetting : ${data.Y_DNTS.toDouble()}" + ", Longitude: ${data.X_CNTS.toDouble()}"
            )
        }
    }

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

    // 전체 마커 map 표시 함수
    private fun displayPOI(data: CrtfcUpsoInfo) {
        with(mapView) {
//            removeAllPOIItems() // 기존 마커들 제거

            for (i in 0 until data.row.size) {
                addPOIItem(addMapPoiMarker(data.row[i])) // 현 마커 추가
                addPOIItem(storeMarker(data.row[i])) // 행사위치 핑
            }
        }
    }

    // 마커 클릭 이벤트
    inner class MarkerEventListener(val context: Context) : MapView.POIItemEventListener {
        override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
            Log.d("로그", "onPOIItemSelected: ${poiItem?.itemName} ")
            mapView?.setMapCenterPoint(poiItem?.mapPoint, false)
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
//        if (binding.storeViewPager.visibility == View.VISIBLE) {
//            // 해당 위치로 지도 중심점 이동, 지도 확대
//            if (marker != null) {
//                val update = CameraUpdateFactory.newMapPoint(marker.mapPoint, 3F)
//                with(mapView) {
//                    animateCamera(
//                        update,
//                        object : net.daum.mf.map.api.CancelableCallback {
//                            override fun onFinish() {
//                                selectPOIItem(marker, true) // 선택한 상점 마커 선택
//                            }
//
//                            override fun onCancel() {
//
//                            }
//                        })
//                }
//            }
//        }
    }
}

