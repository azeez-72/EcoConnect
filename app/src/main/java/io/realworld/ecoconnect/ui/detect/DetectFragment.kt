package io.realworld.ecoconnect.ui.detect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.realworld.ecoconnect.R
import java.io.File
import java.util.concurrent.ExecutorService

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
        return root
    }
}