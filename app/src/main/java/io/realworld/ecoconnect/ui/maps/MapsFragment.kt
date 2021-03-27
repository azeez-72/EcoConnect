package io.realworld.ecoconnect.ui.maps

import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import io.realworld.ecoconnect.R
import io.realworld.ecoconnect.ui.home.HomeViewModel
import java.util.*

class MapsFragment : Fragment() {

    private val TAG = "MAP_FRAGMENT"
    private val geoPoints : MutableList<GeoPoint> = mutableListOf()

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        geoPoints.addAll(mapViewModel.getGeoPoints())

        geoPoints.forEach { geoPoint ->
            Log.d(TAG,"Address : ${geoPoint.latitude} ${geoPoint.longitude}")
            val latLng : LatLng = LatLng(geoPoint.latitude,geoPoint.longitude)
            googleMap.addMarker(MarkerOptions().position(latLng).title("NGO").icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            ))
        }
        val sydney = LatLng(19.285648,72.869232)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney").icon(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        ))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10F))
    }

    private lateinit var mapViewModel: MapsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapViewModel = ViewModelProvider(this).get(MapsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_maps,container,false)

        mapViewModel.addresses.observe(viewLifecycleOwner, androidx.lifecycle.Observer { addresses ->
            for(address in addresses) {
                Log.d(TAG,"Address : ${address.latitude} ${address.longitude}")
            }
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}