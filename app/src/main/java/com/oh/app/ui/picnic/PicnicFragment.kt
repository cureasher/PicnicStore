@file:Suppress("DEPRECATION")

package com.oh.app.ui.picnic

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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.oh.app.common.toastMessage
import com.oh.app.databinding.PicnicFragmentBinding
import com.oh.app.ui.base.BaseFragment
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

const val LBS_CHECK_TAG = "LBS_CHECK_TAG"
const val LBS_CHECK_CODE = 100

class PicnicFragment : BaseFragment<PicnicFragmentBinding>() {
    private lateinit var mapView: MapView
    var longitude = 0.0
    var latitude = 0.0
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
//        checkLocationService()

        mapView = MapView(context)
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
            checkLocationCurrentDevice()
        }
    }

    @SuppressLint("MissingPermission")
    private fun createDefaultMarker(mapView: MapView) {
        var mDefaultMarker = MapPOIItem()
        val name = "현위치"
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location ->
            val latitude = location.latitude
            val longitude = location.longitude
            Log.d("로그", "GPS Location Latitude: $latitude" + ", Longitude: $longitude")
            with(mDefaultMarker) {
                itemName = name
                tag = 0
//            mapPoint = MapPoint.mapPointWithGeoCoord(37.470123, 126.8958453)
                mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
                markerType = MapPOIItem.MarkerType.BluePin
                selectedMarkerType = MapPOIItem.MarkerType.RedPin
                with(mapView) {
                    addPOIItem(mDefaultMarker)
                    selectPOIItem(mDefaultMarker, true)
                    setMapCenterPoint(mapPoint, false)
                }
            }
        }
    }
}