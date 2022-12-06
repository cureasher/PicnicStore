@file:Suppress("DEPRECATION")

package com.oh.app.ui.picnic

import StoreViewModelFactory
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.oh.app.common.toastMessage
import com.oh.app.data.store.CrtfcUpsoInfo
import com.oh.app.data.store.Row
import com.oh.app.databinding.BottomSheetBinding
import com.oh.app.databinding.PicnicFragmentBinding
import com.oh.app.ui.base.BaseFragment
import com.oh.app.ui.picnic.adapter.StoreViewPagerAdapter
import com.oh.app.ui.picnic.repository.StoreRepository
import com.oh.app.ui.picnic.repository.StoreRetrofitService
import net.daum.mf.map.api.*

const val LBS_CHECK_TAG = "LBS_CHECK_TAG"
const val LBS_CHECK_CODE = 100
var storeList: List<Row> = emptyList()
var markerResolver: MutableMap<Row, MapPOIItem> = HashMap()

class PicnicFragment : BaseFragment<PicnicFragmentBinding>() {
    private lateinit var mapView: MapView
    private lateinit var mapPOIItem: MapPOIItem
    private lateinit var innerBind: BottomSheetBinding
    private lateinit var eventListener: MarkerEventListener
    private lateinit var storeAdapter : StoreViewPagerAdapter
    private var mapPosition = 0
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storeViewModel: StoreViewModel = ViewModelProvider(
            this, StoreViewModelFactory(
                StoreRepository(StoreRetrofitService.getInstance())
            )
        ).get(StoreViewModel::class.java)
        storeObserverSetup(storeViewModel)
        storeViewModel.getStoreViewModel()
        eventListener = MarkerEventListener(requireContext())

        innerBind = BottomSheetBinding.inflate(layoutInflater)
        mapView = MapView(context)
        mapView.setPOIItemEventListener(eventListener)
//        createDefaultMarker(mapView)

        binding.mapView.addView(mapView)
        if (isNetworkAvailable()) { // 현재 단말기의 네트워크 가능여부를 알아내고 시작한다
//            checkLocationCurrentDevice()
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
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(locationIntervalTime) // 위치 획득 후 update 되는 최소 주기
                .setMaxUpdateDelayMillis(locationIntervalTime).build() // 위치 획득 후 update delay 최대
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
//            checkLocationCurrentDevice()
        }
    }

    private fun storeObserverSetup(storeViewModel: StoreViewModel) {
        storeViewModel.storeList.observe(viewLifecycleOwner) {
            with(binding.storeViewPager) {
                run {
//                    val storeAdapter = StoreRecyclerAdapter(it, activity as MainActivity)
                    Log.d("로그", "onViewCreated: ${it.StoreInfo.row}")
//                    Log.d("로그", "마커: ${bottomList[0].X_CNTS.toDouble()}, ${bottomList[0].Y_DNTS.toDouble()}, ${bottomList[0].UPSO_NM}")
                    mapSetting(it.StoreInfo.row[0])
                    Log.d("로그", "storeObserverSetup: ${it.StoreInfo.row[0]}")
                    storeList = it.StoreInfo.row
                    storeAdapter = StoreViewPagerAdapter(it.StoreInfo.row)
                    orientation = ViewPager2.ORIENTATION_HORIZONTAL
//                    storeAdapter

//                    Log.d("로그", "onViewCreated: ${it.StoreInfo.row}")
                    storeList.forEachIndexed { index, row ->
                        mapPOIItem = storeMarker(storeList[index])
                        mapView.addPOIItem(mapPOIItem)
                        mapView.selectPOIItem(mapPOIItem, true)
                        adapter = StoreViewPagerAdapter(it.StoreInfo.row)
                        orientation = ViewPager2.ORIENTATION_HORIZONTAL
                    }
//                    storeAdapter
                }
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        if (storeList.isNotEmpty()) {
                            val marker = markerResolver[storeList[position]]
                            // 해당 위치로 지도 중심점 이동, 지도 확대
                            if (marker != null) {
                                val update = CameraUpdateFactory.newMapPoint(marker?.mapPoint, 3F)
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
                })
            }
            with(innerBind.recyclerView) {
                run {
                    Log.d("로그", "onViewCreated: $storeList")
                    val storeAdapter = StoreRecyclerAdapter(storeList)
                    adapter = storeAdapter
                    storeAdapter
                }.refreshRecipeItems()
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
    private fun storeMarker(storeInfo: Row): MapPOIItem {
        var mDefaultMarker = MapPOIItem()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location ->
            with(mDefaultMarker) {
                itemName = storeInfo.UPSO_NM
                tag = 0
                mapPoint = MapPoint.mapPointWithGeoCoord(
                    storeInfo.Y_DNTS.toDouble(),
                    storeInfo.X_CNTS.toDouble()
                )
                markerType = MapPOIItem.MarkerType.BluePin
                selectedMarkerType = MapPOIItem.MarkerType.RedPin
                with(mapView) {
                    addPOIItem(mDefaultMarker)
                    selectPOIItem(mDefaultMarker, true)
                    setMapCenterPoint(mapPoint, false)
                    setZoomLevel(3, true)
                }
                val bounds = MapPointBounds(mapPoint, mapPoint)
                mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds))
            }
        }
        return mDefaultMarker
    }

    // 맵 초기 세팅
    private fun mapSetting(data: Row) {
        with(mapView) {
            mapType = MapView.MapType.Standard
            setZoomLevel(3, true)
            zoomIn(true)
            zoomOut(true)
            addPOIItem(storeMarker(data)) // 행사위치 핑
            setMapCenterPoint(
                MapPoint.mapPointWithGeoCoord(
                    data.Y_DNTS.toDouble(),
                    data.X_CNTS.toDouble()
                ), true
            ) // map 중심점
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
            tag = 0
            markerType = MapPOIItem.MarkerType.RedPin // 마커 색
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
            addPOIItem(storeMarker(data.row[0])) // 행사위치 핑
            for (i in 0 until data.row.size) {
                addPOIItem(addMapPoiMarker(data.row[i])) // 현 마커 추가
            }
        }
    }

    // 마커 클릭 이벤트
    class MarkerEventListener(val context: Context) : MapView.POIItemEventListener {
        override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
            Log.d("로그", "onPOIItemSelected: ${poiItem?.itemName} ")
            mapView?.setMapCenterPoint(poiItem?.mapPoint, true)

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
}

