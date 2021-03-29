package io.realworld.ecoconnect.ui.detect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.realworld.ecoconnect.R

class DetectFragment : Fragment() {

    private lateinit var detectViewModel: DetectViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        detectViewModel =
                ViewModelProvider(this).get(DetectViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_detect, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        detectViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}