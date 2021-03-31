package io.realworld.ecoconnect.ui.maps

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.realworld.ecoconnect.MainActivity
import io.realworld.ecoconnect.R
import io.realworld.ecoconnect.ui.pickup.FormActivity
import kotlinx.coroutines.launch
import java.lang.Exception

class MapsBottomSheetFragment() : BottomSheetDialogFragment() {

    private var TAG = "MAP_BOTTOMSHEET"

    private var pickupButton: Button? = null
    private var ngoId: String? = "form here!"
    private var dataMap: Map<String, Any>? = mutableMapOf()

    private lateinit var mapViewModel: MapsViewModel

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


        try {
            ngoId = this.arguments?.getString("NGO_Id").toString()
        } catch (e: Exception) {
            e.printStackTrace().toString()
        }

            mapViewModel.viewModelScope.launch {
                dataMap = ngoId?.let { mapViewModel.getDataById(it) }

                name?.text = dataMap!!["Name"] as CharSequence?
                address?.text = dataMap!!["address"] as CharSequence?

                pickupButton!!.setOnClickListener {
                    try {
                        val bundle = Bundle()
                        bundle.apply {
                            this.putString("docId",ngoId)
                        }
                        Log.d(TAG, ngoId!!)
                        val intent = Intent(context, FormActivity::class.java)
                        intent.putExtra("docId", ngoId.toString())
                        startActivity(intent)
//                        val navController : NavController =
//                        navController.navigate(R.id.action_mapsBottomSheetFragment_to_pickUpFormFragment)
                    } catch (e: Exception) {
                        Log.i(TAG, "Exception: ${e.message}")
                    }
                }
            }

            return root
        }

    }