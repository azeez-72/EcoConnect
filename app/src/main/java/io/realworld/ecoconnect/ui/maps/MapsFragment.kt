package io.realworld.ecoconnect.ui.maps

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
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

    private val callback = OnMapReadyCallback { googleMap ->

//        val sharedPreferences : SharedPreferences? = context?.getSharedPreferences("sharedPrefs",
//            Context.MODE_PRIVATE
//        )
//        val my_lat : Double? = sharedPreferences?.getString("myLat","19")?.toDouble()
//        val my_lng : Double? = sharedPreferences?.getString("myLng","72")?.toDouble()
//
//        val my_lat_lng : LatLng = LatLng(my_lat!!,my_lng!!)

        val myLatLng = LatLng(17.391050,78.487630)

        googleMap.addMarker(MarkerOptions().position(myLatLng).title("You're here").icon(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        )).tag = "my geopoint"

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,13F))

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
            if(marker.tag != "my geopoint") {
                val bottomSheetFragment = MapsBottomSheetFragment()
                try {
                    bottomSheetFragment.arguments = Bundle().apply {
                        Log.d(TAG,"Bundle here ${marker.tag.toString()}")
                        this.putString("NGO_Id",marker.tag.toString())
                    }
                    bottomSheetFragment.show(childFragmentManager,marker.title)
                } catch(e: Exception) {Log.d(TAG,e.printStackTrace().toString())}
            }
            return@setOnMarkerClickListener true
        }
    }

    private lateinit var mapViewModel: MapsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapViewModel = ViewModelProvider(this).get(MapsViewModel::class.java)
        val root : View? = inflater.inflate(R.layout.fragment_maps,container,false)
//
//        val sharedPreferences : SharedPreferences? = context?.getSharedPreferences("sharedPrefs",Context.MODE_PRIVATE)
//        val sharedLat : String? = sharedPreferences?.getString("myLat","latitude to be assigned")
//        val sharedLng : String? = sharedPreferences?.getString("myLng","longitude to be assigned")
//
//        Log.d(TAG, "check $sharedLat $sharedLng")


//        sharedPreferences?.getString("myLat","").apply {
//            this?.let { Log.d(TAG, it) }
//            this?.let { Log.d(TAG, it) }
//            this?.let { Log.d(TAG, it) }
//        }

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