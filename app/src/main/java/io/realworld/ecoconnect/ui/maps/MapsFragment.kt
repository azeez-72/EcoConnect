package io.realworld.ecoconnect.ui.maps

import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.GeoPoint
import io.realworld.ecoconnect.R
import io.realworld.ecoconnect.ui.home.HomeViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*

class MapsFragment : Fragment() {

    private val TAG = "MAP_FRAGMENT"
    private val ngoData : MutableList<Map<String,Any>>? = mutableListOf()
    private var name : String? = "Loading"

    private val callback = OnMapReadyCallback { googleMap ->

        for(data in ngoData!!) {
            Log.d(TAG,"Data entering map : $data")
        }

        ngoData.forEach { ngo_data ->
            Log.d(TAG,"Map loop")
            name = ngo_data["Name"] as String?
            Log.d(TAG,"Name in Loop : $name")
            Log.d(TAG,"Name in Loop : $name")
            Log.d(TAG,"Name in Loop : $name")
            val address : String? = ngo_data["Address"] as String?
            val geoPoint : GeoPoint? = ngo_data["Location"] as GeoPoint?
            if(geoPoint != null) {
                val latLng : LatLng = LatLng(geoPoint.latitude, geoPoint.longitude)

                googleMap.addMarker(MarkerOptions().position(latLng).title(name).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                ))
            }
        }
        val homeLoc = LatLng(19.285648,72.869232)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLoc,10F))


        googleMap.setOnMarkerClickListener { marker->
            val bottomSheet : MapsBottomSheet = MapsBottomSheet()
            bottomSheet.show(childFragmentManager,marker.title)

            bottomSheet.arguments = Bundle().apply {
                this.putString("NGO_Name",name)
                this.putString("NGO_Id","xmdres")
                this.putString("NGO_Address","Chameli Nagar,Dadar East")
                this.putInt("NGO_PickUpTime",23)
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

        mapViewModel.O_ngoData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { ngo_data ->
            for(data in ngo_data) {
                val geoPoint : GeoPoint? = data["Location"] as? GeoPoint
                Log.d(TAG,"observing Address : ${geoPoint?.latitude} ${geoPoint?.longitude}")
            }
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        suspend fun getNGOData() {
//            mapViewModel.reference.get().await().forEach {
//                Log.d(TAG, "Data from test : ${it.data}")
//                ngoData!!.add(it.data)
//            }
//        }
//
//        GlobalScope.launch {
//            suspend {
//                withContext(Dispatchers.Main) {
//                    Log.d(TAG,"Start")
//                    getNGOData()
//                    Log.d(TAG,"End")
//
//                    if(ngoData!!.isEmpty()) Log.d(TAG,"Empty data")
//
//                    for(data in ngoData) {
//                        Log.d(TAG,"Data after coroutine : $data")
//                    }
//                    delay(3000)
//                }
//            }.invoke()
//        }

        mapViewModel.viewModelScope.launch {
            ngoData!!.addAll(mapViewModel.getNGOData())

            Log.d(TAG, "We're about to map")

            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)
        }
    }
}