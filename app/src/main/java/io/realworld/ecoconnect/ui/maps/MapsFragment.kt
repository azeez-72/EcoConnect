package io.realworld.ecoconnect.ui.maps

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.GeoPoint
import io.realworld.ecoconnect.R
import kotlinx.coroutines.*
import java.util.*

class MapsFragment : Fragment() {

    private val TAG = "MAP_FRAGMENT"
    private val ngoData : MutableList<Map<String,Any>> = mutableListOf()
    private var name : String? = "Loading"
    private var address : String? = "Address"
    private var id : String? = "id"

//    private lateinit var locationListener : LocationListener
//    private lateinit var locationManager : LocationManager
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "Permission granted")
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0F,locationListener)
//            }
//        }
//    }

    private val callback = OnMapReadyCallback { googleMap ->

        ngoData.forEach { ngo_data ->
            name = ngo_data["Name"] as String?
            address = ngo_data["address"] as String?

            id = ngo_data["id"] as String?

            val geoPoint : GeoPoint? = ngo_data["Location"] as GeoPoint?
            if(geoPoint != null) {
                val latLng = LatLng(geoPoint.latitude, geoPoint.longitude)

                googleMap.addMarker(MarkerOptions().position(latLng).title(name).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )).tag = id
            }
        }


        googleMap.setOnMarkerClickListener { marker->

            val bottomSheetFragment = MapsBottomSheetFragment()
            try {
                bottomSheetFragment.show(childFragmentManager,marker.title)
            } catch(e: Exception) {Log.d(TAG,e.printStackTrace().toString())}

            bottomSheetFragment.arguments = Bundle().apply {
                this.putString("NGO_Id",marker.tag.toString())
            }

            return@setOnMarkerClickListener true
        }
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        locationListener = LocationListener { location -> Log.d(TAG,"${location.latitude} ${location.longitude}") }
//
//        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),100)
//        } else {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0F,locationListener)
//        }
//    }

    private lateinit var mapViewModel: MapsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapViewModel = ViewModelProvider(this).get(MapsViewModel::class.java)
        val root : View? = inflater.inflate(R.layout.fragment_maps,container,false)

        mapViewModel.O_ngoData.observe(viewLifecycleOwner, { ngo_data ->
            for(data in ngo_data) {
                val geoPoint : GeoPoint? = data["Location"] as? GeoPoint
                Log.d(TAG,"observing Address : ${geoPoint?.latitude} ${geoPoint?.longitude}")
            }
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mapViewModel.viewModelScope.launch {

            try {
                mapViewModel.getNGOData()?.let { ngoData.addAll(it) }
            } catch(e:Exception) {e.printStackTrace().toString()}

            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment?
            mapFragment?.getMapAsync(callback)
        }
    }
}