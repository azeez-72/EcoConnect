package io.realworld.ecoconnect.ui.pickup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.realworld.ecoconnect.R

class PickUpFormFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root : View? = inflater.inflate(R.layout.fragment_pickupform,container,false)

        val textView : TextView? = root?.findViewById(R.id.textView2)
        val docId : String? = savedInstanceState?.getString("docId")

        textView?.text = docId

        return root
    }
}