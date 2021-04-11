package io.realworld.ecoconnect.ui.pickup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.realworld.ecoconnect.MainActivity
import io.realworld.ecoconnect.R
import io.realworld.ecoconnect.databinding.ActivityFormBinding
import kotlinx.coroutines.runBlocking

class FormActivity() : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityFormBinding
    private var TAG = "FORM_ACTIVITY"
    val db=Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ngoId = intent.extras?.getString("docId")

        binding.button.setOnClickListener {
            if (ngoId != null) {
                sendRequest(binding, ngoId)
            }
        }

        Log.d(TAG,ngoId.toString())

        binding.textView.text = "NGO"
        binding.pickupTimeView.text = "10AM-12PM"

        runBlocking {
            db.collection("NGO Addresses").document(ngoId!!).get().addOnSuccessListener { docSnap->
                binding.textView.text = docSnap.data?.get("Name") as CharSequence?
                binding.pickupTimeView.text = docSnap.data?.get("pickupTime") as CharSequence?
            }
        }

    }

    fun sendRequest(binding : ActivityFormBinding, ngoId: String)
    {
        if(binding.editTextName.text.toString().isEmpty())
        {
            binding.editTextName.error="Enter Name"
            binding.editTextName.requestFocus()
            return
        }

        if(binding.editTextAmount.text.toString().isEmpty())
        {
            binding.editTextAmount.error="Enter Name"
            binding.editTextAmount.requestFocus()
            return
        }

        if(binding.editTextDescription.text.toString().isEmpty())
        {
            binding.editTextDescription.error="Enter Name"
            binding.editTextDescription.requestFocus()
            return
        }

        if(binding.editTextPhoneNumber.text.toString().isEmpty())
        {
            binding.editTextPhoneNumber.error="Enter Name"
            binding.editTextPhoneNumber.requestFocus()
            return
        }

        val mAuth=FirebaseAuth.getInstance()
<<<<<<< HEAD
        db.collection("NGO Addresses").document(ngoId).collection("Pickups").document(mAuth.currentUser.uid)
            .set(
=======
        db.collection("NGO Addresses").document(ngoId).collection("Pickups")
            .add(
>>>>>>> 89347ad808d46bcccbe9166a6c82a8cf5e969bbf
                hashMapOf(
                    "name" to binding.editTextName.text.toString().trim(),
                    "uid" to mAuth.currentUser.uid,
                    "phone" to binding.editTextPhoneNumber.text.toString().trim(),
                    "weight" to binding.editTextAmount.text.toString(),
                    "description" to binding.editTextDescription.text.toString().trim()
                )
            )
            .addOnSuccessListener { doc ->
                db.collection("users").document(mAuth.currentUser.uid).collection("Pickups")
                    .document(doc.id).set(
                        hashMapOf(
                            "ngo id" to ngoId,
                            "name" to binding.textView.text,
                            "weight" to binding.editTextAmount.text.toString(),
                            "status" to "pending"
                        )
                    ).addOnSuccessListener {
                        Toast.makeText(this, "Request sent", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Unable to process request", Toast.LENGTH_SHORT).show()
            }
    }

}