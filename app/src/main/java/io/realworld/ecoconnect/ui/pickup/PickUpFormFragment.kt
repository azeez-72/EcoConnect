package io.realworld.ecoconnect.ui.pickup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.realworld.ecoconnect.R
import kotlinx.coroutines.launch

class PickUpFormFragment : Fragment() {

    private lateinit var pickUpFormViewModel: PickUpFormViewModel
    private var TAG = "PICKUPFORM"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pickUpFormViewModel = ViewModelProvider(this).get(PickUpFormViewModel::class.java)
        val root : View? = inflater.inflate(R.layout.fragment_pickupform,container,false)

        val textView : TextView? = root?.findViewById(R.id.textView2)

        pickUpFormViewModel.viewModelScope.launch {
            val bundle = Bundle()
            val docId : String? = bundle.getString("docId")

            Log.d(TAG,"Bundle data : $docId")

            if(!docId.isNullOrBlank()) textView?.text = docId
        }

        return root
    }
}