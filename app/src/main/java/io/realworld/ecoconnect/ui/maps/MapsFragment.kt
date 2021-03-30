package io.realworld.ecoconnect.ui.maps

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private val ngoData : MutableList<Map<String,Any>>? = mutableListOf()
    private var name : String? = "Loading"
    private var address : String? = "Address"
    private var id : String? = "id"

    private val callback = OnMapReadyCallback { googleMap ->

        ngoData?.forEach { ngo_data ->
            name = ngo_data["Name"] as String?
            address = ngo_data["address"] as String?

            id = ngo_data["id"] as String?

            val geoPoint : GeoPoint? = ngo_data["Location"] as GeoPoint?
            if(geoPoint != null) {
                val latLng : LatLng = LatLng(geoPoint.latitude, geoPoint.longitude)

                googleMap.addMarker(MarkerOptions().position(latLng).title(name).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )).tag = id
            }
        }


        googleMap.setOnMarkerClickListener { marker->

            val bottomSheetFragment : MapsBottomSheetFragment = MapsBottomSheetFragment()
            bottomSheetFragment.show(childFragmentManager,marker.title)

            bottomSheetFragment.arguments = Bundle().apply {
                this.putString("NGO_Id",marker.tag.toString())
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


        mapViewModel.viewModelScope.launch {
            ngoData!!.addAll(mapViewModel.getNGOData())

            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)
        }
    }
}