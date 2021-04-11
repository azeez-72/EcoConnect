package io.realworld.ecoconnect.ui.maps

import android.app.Application
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.realworld.ecoconnect.MainActivity
import io.realworld.ecoconnect.R
import io.realworld.ecoconnect.ui.pickup.FormActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class MapsBottomSheetFragment() : BottomSheetDialogFragment() {

    private var TAG = "MAP_BOTTOMSHEET"

    private var pickupButton: Button? = null
    private var ngoId: String? = "form here!"
    private var dataMap: Map<String, Any>? = mutableMapOf()

    val mAuth = FirebaseAuth.getInstance()
    val db= Firebase.firestore
    var disabled = true
    var exists = false

    private lateinit var mapViewModel: MapsViewModel

    private fun isScheduled(ngoId : String) {
        db.collection("NGO Addresses").document(ngoId).collection("Pickups").whereEqualTo("uid",mAuth.currentUser.uid)
            .get().addOnSuccessListener { document ->
                if(document.size() != 0)
                {
                    pickupButton!!.text = "CANCEL PICKUP"
                    pickupButton!!.setBackgroundColor(Color.RED)
                    pickupButton!!.setOnClickListener {
                        deletePickUpEntry(ngoId!!)
                    }
                    pickupButton!!.isClickable=true

                }
                else
                {

                pickupButton?.setOnClickListener{
                        try {
                            val bundle = Bundle()
                            bundle.apply {
                                this.putString("docId", ngoId)
                            }
                            Log.d(TAG, ngoId)
                            val intent = Intent(context, FormActivity::class.java)
                            intent.putExtra("docId", ngoId)
                            startActivity(intent)
//                        val navController : NavController =
//                        navController.navigate(R.id.action_mapsBottomSheetFragment_to_pickUpFormFragment)
                        } catch (e: Exception) {
                            Log.i(TAG, "Exception: ${e.message}")
                        }
                    }
                    pickupButton!!.isClickable=true
                }
            }
    }

    private fun deletePickUpEntry(ngoId: String) {
        db.collection("NGO Addresses").document(ngoId).collection("Pickups")
            .whereEqualTo("uid",mAuth.currentUser.uid).get()
            .addOnSuccessListener {document ->
                document.forEach{ doc ->
                    db.collection("NGO Addresses").document(ngoId).collection("Pickups").document(doc.id).delete()
                        .addOnSuccessListener {
                            db.collection("users").document(mAuth.currentUser.uid)
                                .collection("Pickups").document(doc.id).update("status","cancelled")
                                .addOnSuccessListener {
                                    Toast.makeText(this.requireContext(), "Pickup Cancelled", Toast.LENGTH_SHORT).show()
                                    pickupButton?.text="Pickup"
                                    pickupButton!!.setBackgroundColor(Color.parseColor("#FF045A08"))
                                    pickupButton?.setOnClickListener {
                                        try {
                                            val bundle = Bundle()
                                            bundle.apply {
                                                this.putString("docId", ngoId)
                                            }
                                            Log.d(TAG, ngoId)
                                            val intent = Intent(context, FormActivity::class.java)
                                            intent.putExtra("docId", ngoId)
                                            startActivity(intent)
                                //                        val navController : NavController =
                                //                        navController.navigate(R.id.action_mapsBottomSheetFragment_to_pickUpFormFragment)
                                        } catch (e: Exception) {
                                            Log.i(TAG, "Exception: ${e.message}")
                                        }
                                    }
                                    pickupButton!!.isClickable=true
                                }
                        }
                }

            }
            .addOnFailureListener {
                Toast.makeText(context,"Unable to cancel pickup",Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapViewModel = ViewModelProvider(this).get(MapsViewModel::class.java)
        val root: View? = inflater.inflate(R.layout.fragment_mapbottomsheet, container, false)

        val name: TextView? = root?.findViewById(R.id.name_text)
        val address: TextView? = root?.findViewById(R.id.address_text)
        val pickupTime: TextView? = root?.findViewById(R.id.pickupTime_text)
        pickupButton = root?.findViewById(R.id.pickup_button)
        pickupButton?.isClickable  = false


        try {
            ngoId = this.arguments?.getString("NGO_Id").toString()
            isScheduled(ngoId!!)
        } catch (e: Exception) {
            e.printStackTrace().toString()
        }

            mapViewModel.viewModelScope.launch {

                dataMap = ngoId?.let { mapViewModel.getDataById(it) }

                name?.text = dataMap!!["Name"] as CharSequence?
                address?.text = dataMap!!["address"] as CharSequence?

                if(dataMap!!["pickupTime"] == null) {
                    pickupTime?.text = "No PickUp Available"
                    pickupTime?.setTextColor(Color.RED)
                } else {
                    pickupTime?.text = dataMap!!["pickupTime"] as CharSequence?
                }
            }

            return root
        }

    }