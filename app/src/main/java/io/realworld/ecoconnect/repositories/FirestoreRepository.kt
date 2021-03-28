package io.realworld.ecoconnect.repositories

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.LatLng

class FirestoreRepository {
    val TAG = "FIREBASE_REPOSITORY"
    val firestoreDB = FirebaseFirestore.getInstance()

    fun getAddRef(): CollectionReference {
        val addRef = firestoreDB.collection("NGO Addresses")
        return addRef
    }
}