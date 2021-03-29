package io.realworld.ecoconnect

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import io.realworld.ecoconnect.ui.detect.ImageClassifier
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mAuth: FirebaseAuth
//    private var firebasePerformance = FirebasePerformance.getInstance()
//    private lateinit var remoteConfig: FirebaseRemoteConfig
//    private var imageClassifier = ImageClassifier(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        val user=mAuth.currentUser
//        if(user==null)
//        {
//            val loginSignupIntent = Intent(this, LoginSignUpActivity::class.java)
//
//            loginSignupIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//
//
//            startActivity(loginSignupIntent)
//        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_maps), drawerLayout)
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

//    private fun setupImageClassifier() {
//            configureRemoteConfig()
//            remoteConfig.fetchAndActivate()
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val modelName = remoteConfig.getString("trashnet_model")
//                        val downloadTrace = firebasePerformance.newTrace("download_model")
//                        downloadTrace.start()
//                        downloadModel("trashnet_model")
//                            .addOnSuccessListener {
//                                downloadTrace.stop()
//                            }
//                    } else {
//                        showToast("Failed to fetch model.")
//                    }
//                }
//    }
//
//    private fun configureRemoteConfig() {
//        remoteConfig = Firebase.remoteConfig
//        val configSettings = remoteConfigSettings {
//            minimumFetchIntervalInSeconds = 3600
//        }
//        remoteConfig.setConfigSettingsAsync(configSettings)
//    }
//
//    private fun downloadModel(modelName: String): Task<Void> {
//        val remoteModel = FirebaseCustomRemoteModel.Builder(modelName).build()
//        val firebaseModelManager = FirebaseModelManager.getInstance()
//        return firebaseModelManager
//            .isModelDownloaded(remoteModel)
//            .continueWithTask { task ->
//                // Create update condition if model is already downloaded, otherwise create download
//                // condition.
//                val conditions = if (task.result != null && task.result == true) {
//                    FirebaseModelDownloadConditions.Builder()
//                        .requireWifi()
//                        .build() // Update condition that requires wifi.
//                } else {
//                    FirebaseModelDownloadConditions.Builder().build(); // Download condition.
//                }
//                firebaseModelManager.download(remoteModel, conditions)
//            }
//            .addOnSuccessListener {
//                firebaseModelManager.getLatestModelFile(remoteModel)
//                    .addOnCompleteListener {
//                        val model = it.result
//                        if (model == null) {
//                            showToast("Failed to get model file.")
//                        } else {
//                            showToast("Downloaded remote model: $modelName")
//                            imageClassifier.initialize(model)
//                        }
//                    }
//            }
//            .addOnFailureListener {
//                showToast("Model download failed for $modelName, please check your connection.")
//            }
//    }
//
//    @Throws(IOException::class)
//    private fun loadModelFile(): ByteBuffer {
//        val fileDescriptor = assets.openFd(MainActivity.MODEL_FILE)
//        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
//        val fileChannel = inputStream.channel
//        val startOffset = fileDescriptor.startOffset
//        val declaredLength = fileDescriptor.declaredLength
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//    }
//
//    private fun showToast(text: String) {
//        Toast.makeText(
//            this,
//            text,
//            Toast.LENGTH_SHORT
//        ).show()
//    }

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