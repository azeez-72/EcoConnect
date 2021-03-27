package io.realworld.ecoconnect.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.GeoPoint
import com.google.type.LatLng
import io.realworld.ecoconnect.repositories.FirestoreRepository

class MapsViewModel : ViewModel() {

    private val TAG = "MAP_VIEWMODEL"

    private val fireRepo : FirestoreRepository = FirestoreRepository()
    private val addressList : MutableList<GeoPoint> = mutableListOf()
    private val addRef = fireRepo.getAddRef()

    fun getGeoPoints() : MutableList<GeoPoint> {
        addRef.get().addOnSuccessListener { query ->
            query.documents.forEach { doc->
                val geoPoint: GeoPoint? = doc.data?.get("Location") as? GeoPoint
                Log.d(TAG,"lat : ${geoPoint?.latitude} lng : ${geoPoint?.longitude}")

                if(geoPoint != null) {
                    addressList.add(geoPoint)
                }
            }
        }
            .addOnFailureListener {
                Log.d(TAG,"Oops! Error! - ${it.message}",it)
            }
        return addressList
    }

    private val _addresses = MutableLiveData<List<GeoPoint>>().apply {
        value = addressList
    }

    val addresses : LiveData<List<GeoPoint>> = _addresses
}