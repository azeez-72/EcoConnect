package io.realworld.ecoconnect.ui.pickup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import io.realworld.ecoconnect.R
import io.realworld.ecoconnect.databinding.ActivityFormBinding

class FormActivity() : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityFormBinding
    private var TAG = "FORM_ACTIVITY"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ngoId = intent.extras?.getString("docId")

        Log.d(TAG,ngoId.toString())

        binding.textView.text = ngoId.toString()

    }
}