package io.realworld.ecoconnect

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.location.LocationListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import io.realworld.ecoconnect.ui.detect.ImageClassifier
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mAuth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun navigate() {
            val loginSignupIntent = Intent(this, LoginSignUpActivity::class.java)

            loginSignupIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginSignupIntent)
            finish()
        }

        fun showData() {
            val OrganizationIntent = Intent(this, Organization::class.java)
            OrganizationIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(OrganizationIntent)
        }

        fun showToast() {
            Toast.makeText(this, "Unable to connect to database", Toast.LENGTH_SHORT)
                .show()
        }

        mAuth = FirebaseAuth.getInstance()

        val user : FirebaseUser? = mAuth.currentUser

        Log.d("main thing this is intolerable",user.toString())

        if(user==null)
        {
            Log.d("main thing this is intolerable",user.toString())

            navigate()
        }

        else
        {
            val db = Firebase.firestore
            db.collection("NGO Addresses").document(user.uid).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result.exists()) {
                            showData()
                        }
                    } else {
                        showToast()
                    }

                }
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        try {
            val navView = findViewById<NavigationView>(R.id.nav_view)
            val header = navView.getHeaderView(0)
            var uri : Uri? = mAuth.currentUser!!.photoUrl
//            val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
//            val source = uri?.let { ImageDecoder.createSource(this.contentResolver, it) }
//            val bitmap = source?.let { ImageDecoder.decodeBitmap(it) }

            Glide.with(this.applicationContext).load(uri).into(header.findViewById(R.id.image_drawer))
            header.findViewById<TextView>(R.id.name_drawer).text = mAuth.currentUser!!.displayName
            header.findViewById<TextView>(R.id.email_drawer).text = mAuth.currentUser!!.email
        } catch(e : IOException) {
            println(e)
        }

        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home,R.id.nav_slideshow,R.id.nav_maps), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
//        setupImageClassifier()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
        private const val TAG = "MainActivity"
        const val MODEL_FILE = "trashnet_model.tflite"
    }

}