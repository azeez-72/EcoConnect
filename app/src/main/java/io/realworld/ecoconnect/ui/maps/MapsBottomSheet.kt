package io.realworld.ecoconnect.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.realworld.ecoconnect.R

class MapsBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root : View? = inflater.inflate(R.layout.fragment_mapbottomsheet,container,false)

        val name_text : TextView? = root?.findViewById(R.id.name_text)
        val ngo_name : String? = this.arguments?.getString("NGO_Name")

        name_text?.text = ngo_name

        return root
    }
}