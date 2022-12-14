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


        // ?????? ?????? ?????? true
        with(binding) {
            martChip.isChecked = true
            restaurantChip.isChecked = true
        }

        binding.mapView.addView(mapView)
        if (isNetworkAvailable()) { // ?????? ???????????? ???????????? ??????????????? ???????????? ????????????
            checkLocationCurrentDevice()
        } else {
            Log.e(LBS_CHECK_TAG, "????????? ???????????? ??????!")
            toastMessage("???????????? ???????????? ?????? ???????????????.")
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
                // ?????? ???????????? ????????????(wifi, data ??????)
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // ???????????? ???????????? (ex IoT ?????? ???)
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                // ???????????? ????????? ????????????
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
                 * ????????? ????????? ????????? : true ??????
                 * ??????, ???????????? ?????? ????????? ??? ??????
                 */
                .setWaitForAccurateLocation(true).build()
//                .setMinUpdateIntervalMillis(locationIntervalTime) // ?????? ?????? ??? update ?????? ?????? ??????
//                .setMaxUpdateDelayMillis(locationIntervalTime).build() // ?????? ?????? ??? update delay ??????
        val lbsSettingsRequest: LocationSettingsRequest =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val settingClient: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val taskBSSettingResponse: Task<LocationSettingsResponse> =
            settingClient.checkLocationSettings(lbsSettingsRequest)
        // ???????????? ????????? on??? ??????
        taskBSSettingResponse.addOnSuccessListener {
            with(mapView) {
                currentLocationTrackingMode =
                    MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
                createDefaultMarker(mapView)
            }
        }

        // ?????? ?????? ????????? off??? ??????
        taskBSSettingResponse.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    toastMessage("???????????? ????????? ????????? On ???????????? ??????!")
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


    // ?????? ?????? ??????
    @SuppressLint("MissingPermission")
    private fun createDefaultMarker(mapView: MapView) {
        var mDefaultMarker = MapPOIItem()
        val name = "?????? ??????"

        MartInit.fusedMyLocation()

        // ?????????????????? ??????
        var latitude = PicnicStoreApplication.pref.getString("latitude", "nolatitude")
        var longitude = PicnicStoreApplication.pref.getString("longitude", "nolongitude")

        Log.d("??????", "????????? ?????? ??????: ${latitude}, ${longitude}")
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

    // ?????? ?????? ??????
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
        Log.d("??????", "????????????: ${MartInit.getMart(guInfo)} ")
        val martNameList = MartInit.getMart(guInfo)

        martPoiInfo?.forEach { storeName, _ ->
            martNameList!!.forEach {
                if (storeName == it) {
//                    Log.d("??????", "storeObserverSetup: $s, ${mapPoint}")
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
        Log.d("??????", "martMapSetting: ${storeList.size} storelist ??? ??????")
//        val guInfo = PicnicStoreApplication.pref.getString("guInfo", "noguInfo")

        Log.d("??????", "martMapSetting: ${MartInit.getMart(guInfo)} ")
    }

    // ??? ?????? ??????
//    private fun mapSetting(data: Row) {
//        with(mapView) {
//            mapType = MapView.MapType.Standard
//            setZoomLevel(3, true)
//            zoomIn(true)
//            zoomOut(true)
//            addPOIItem(storeMarker(data)) // ???????????? ???
//        }
//    }

    // ???????????? ????????? ???????????? ??????
    private fun addMapPoiMarker(data: Row): MapPOIItem {
        val marker = MapPOIItem()
        with(marker) {
            tag = 2
            markerType = MapPOIItem.MarkerType.CustomImage // ?????? ???
            selectedMarkerType = MapPOIItem.MarkerType.CustomImage
            customImageResourceId = R.drawable.rice_mart_icon
            customSelectedImageResourceId = R.drawable.rice_mart_select
            mapPoint = MapPoint.mapPointWithGeoCoord(
                data.Y_DNTS.toDouble(),
                data.X_CNTS.toDouble()
            ) // poi?????? ??????
            itemName = data.UPSO_NM // ?????????
        }
        markerResolver[data] = marker
        return marker
    }

    // ?????? ?????? map ?????? ?????? ?????? ?????? ??????
    @RequiresApi(Build.VERSION_CODES.N)
    private fun displayPOI(data: List<Row>) {

        with(mapView) {
            removeAllPOIItems() // ?????? ????????? ??????
            for (i in 0 until data.size) {
                addPOIItem(addMapPoiMarker(data[i])) // ??? ?????? ??????
                addPOIItem(storeMarker(data[i])) // ???????????? ???
            }
            // ?????? ?????? ??????
            martMapSetting(data[0].CGG_CODE_NM)

        }
    }

    // ?????? ?????? ?????????
    inner class MarkerEventListener(val context: Context) : MapView.POIItemEventListener {
        override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
            Log.d("??????", "onPOIItemSelected: ${poiItem?.itemName} ")
//            mapView?.setMapCenterPoint(poiItem?.mapPoint, false)
            binding.storeViewPager.visibility = View.VISIBLE
            binding.mapOverlay.visibility = View.VISIBLE

            // ?????? ????????? ??? ??????
            Log.d(
                "??????",
                "????????? ????????? ???: ${storeList.map { row -> row.UPSO_NM }.indexOf(poiItem?.itemName)}"
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
            // ????????? ?????????
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
            // ????????? ?????? ??? isDraggable = true ??? ??? ????????? ??????????????? ??????
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
        Log.d("??????", "onItemClick: ?????????")
        val marker = markerResolver[storeList[pos]]
        if (binding.storeViewPager.visibility == View.VISIBLE) {
            // ?????? ????????? ?????? ????????? ??????, ?????? ??????
            if (marker != null) {
                val update = CameraUpdateFactory.newMapPoint(marker.mapPoint, 3F)
                with(mapView) {
                    animateCamera(
                        update,
                        object : net.daum.mf.map.api.CancelableCallback {
                            override fun onFinish() {
                                selectPOIItem(marker, true) // ????????? ?????? ?????? ??????
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
                    Log.d("??????", "????????? onItemSelected: ${spinner.getItemAtPosition(pos)} ")
                    storeViewModelSetting(gu.toString())
//                    storeViewModel.getStoreViewModel(guInfo)
//                    storeViewModel.guValue.value = parent?.getItemAtPosition(pos).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    parent?.getItemAtPosition(getIndex(binding.locationSpinner, "?????????"))
                    Log.d("??????", "onNothingSelected: ????????? ??????")
                }
            }
        }
    }

    fun getIndex(spinner: Spinner, item: String): Int {
        for (i in 0..spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == item) {
                Log.d("??????", "getIndex: $i $item")
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
//                OssLicensesMenuActivity.setActivityTitle("???????????? ???????????? ??????")
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

            Log.d("??????", "storeObserverSetup: ${it.StoreInfo.row}")
            with(storeViewPager) {
                run {
                    displayPOI(storeList)
                    storeAdapter = StoreViewPagerAdapter(it.StoreInfo.row, this@PicnicFragment)
//                    storeAdapter.setItemClickListener(object :
//                        StoreViewPagerAdapter.OnItemClickListener {
//                        override fun onItemClick(pos: Int) {
//                            Log.d("??????", "onItemClick: $pos")
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
                                // ?????? ????????? ?????? ????????? ??????, ?????? ??????
                                if (marker != null) {
                                    val update =
                                        CameraUpdateFactory.newMapPoint(marker.mapPoint, 3F)
                                    with(mapView) {
                                        animateCamera(
                                            update,
                                            object : net.daum.mf.map.api.CancelableCallback {
                                                override fun onFinish() {
                                                    selectPOIItem(marker, true) // ????????? ?????? ?????? ??????
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
        binding.progressBar.visibility = View.GONE // ??????

        storeViewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            Log.e("??????", it)
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


