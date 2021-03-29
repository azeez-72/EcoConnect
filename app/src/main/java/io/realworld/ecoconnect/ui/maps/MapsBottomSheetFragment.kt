package io.realworld.ecoconnect.ui.maps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.realworld.ecoconnect.R
import kotlinx.coroutines.launch
import java.lang.Exception

class MapsBottomSheetFragment : BottomSheetDialogFragment() {

    private var TAG = "MAP_BOTTOMSHEET"

    private var pickupButton : Button? = null
    lateinit var ngoId : String
    private var dataMap : Map<String,Any> = mutableMapOf()

    private lateinit var mapViewModel: MapsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapViewModel = ViewModelProvider(this).get(MapsViewModel::class.java)
        val root : View? = inflater.inflate(R.layout.fragment_mapbottomsheet,container,false)

        val name : TextView? = root?.findViewById(R.id.name_text)
        val address : TextView? = root?.findViewById(R.id.address_text)
        val pickupTime : TextView? = root?.findViewById(R.id.pickupTime_text)
        pickupButton = root?.findViewById(R.id.pickup_button)

        val ngoId : String? = this.arguments?.getString("NGO_Id")

        mapViewModel.viewModelScope.launch {
            if (ngoId != null) {
                dataMap = mapViewModel.getDataById(ngoId)

                name?.text = dataMap["Name"] as CharSequence?
                address?.text = dataMap["address"] as CharSequence?
            }
        }

        pickupButton?.setOnClickListener {
            val bundle : Bundle = Bundle(1)
            bundle.putString("docId",ngoId)
            root?.findNavController()?.navigate(R.id.action_mapsBottomSheetFragment_to_pickUpFormFragment)
        }

        return root
    }

}