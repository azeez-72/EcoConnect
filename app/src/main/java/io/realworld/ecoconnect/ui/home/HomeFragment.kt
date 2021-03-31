package io.realworld.ecoconnect.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.Intent
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
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import io.realworld.ecoconnect.LoginSignUpActivity
import io.realworld.ecoconnect.MainActivity
import io.realworld.ecoconnect.R
import kotlinx.coroutines.launch

class HomeFragment : Fragment(),LocationListener {

    var locationManager: LocationManager? = null
    private val TAG = "HOME_FRAGMENT"

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }

        val getLocButton : Button? = root?.findViewById(R.id.getLocationButton)

        getLocButton?.alpha = 0f

//        val sharedPreferences : SharedPreferences? = context?.getSharedPreferences("sharedPrefs", MODE_PRIVATE)

//        val firstTime : Boolean? = sharedPreferences?.getBoolean("firstTime",true)

//        if(!firstTime!!) {getLocButton?.alpha = 0f }

//        root?.findViewById<Button>(R.id.getLocationButton)?.setOnClickListener {
//            val sharedPreferences : SharedPreferences? = context?.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
//            val editor : SharedPreferences.Editor? = sharedPreferences?.edit()
//
//            homeViewModel.viewModelScope.launch {
//                editor?.apply { putBoolean("firstTime",true) }?.apply()
//
//                Log.d(TAG,sharedPreferences?.getBoolean("firstTime 1",false).toString())
//
//                getLocation()
//
//                Log.d(TAG,sharedPreferences?.getBoolean("firstTime 2",true).toString())
//
//                editor?.apply {putBoolean("firstTime",false)}?.apply()
//            }
//        }

        root.findViewById<Button>(R.id.signoutButton).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val loginSignupIntent = Intent(activity, LoginSignUpActivity::class.java)
            loginSignupIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginSignupIntent)
        }

        return root
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        try {
            locationManager = context?.getSystemService(
                LOCATION_SERVICE
            ) as LocationManager
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                10f,
                this
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLocationChanged(location: Location) {
//
//        val sharedPreferences : SharedPreferences? = context?.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
//        if(sharedPreferences?.getBoolean("firstTime",true)!!) {
//            val editor : SharedPreferences.Editor? = sharedPreferences.edit()
//            Log.d(TAG,"${location.latitude} ${location.longitude}")
//
//            editor?.apply {
//                putString("myLat",location.latitude.toString())
//                putString("myLng",location.longitude.toString())
//            }?.apply()
//        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}
}