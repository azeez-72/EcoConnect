package io.realworld.ecoconnect.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.type.LatLng
import io.realworld.ecoconnect.repositories.FirestoreRepository
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import java.util.*

class MapsViewModel : ViewModel() {

    var reference: CollectionReference = FirebaseFirestore.getInstance().collection("NGO Addresses")

    private val TAG = "MAP_VIEWMODEL"

    private val ngoData : MutableList<Map<String, Any>>? = mutableListOf()

    private val fireRepo : FirestoreRepository = FirestoreRepository()

    private var dataMap : Map<String,Any>? = mutableMapOf()

    suspend fun getNGOData() : List<Map<String,Any>>? {
        try {
            reference.get().await().forEach {
                Log.d(TAG,"Data from test : ${it.data}")
                ngoData?.add(it.data)
            }
        } catch (e:Exception) {Log.d(TAG,"Exception : $e")}

        return ngoData
    }

    suspend fun getDataById(id : String) : Map<String,Any>? {
        try {
            dataMap = reference.document(id).get().await().data
            Log.d("TAG",dataMap.toString())
        } catch (e:Exception) {Log.d(TAG,"Exception : $e")}

        return dataMap
    }

    private val _ngoData = MutableLiveData<List<Map<String,Any>>>().apply {
        value = ngoData
    }

    val O_ngoData : LiveData<List<Map<String,Any>>> = _ngoData
}