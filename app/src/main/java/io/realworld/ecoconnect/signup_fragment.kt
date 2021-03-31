package io.realworld.ecoconnect

import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.RestrictionsManager.RESULT_ERROR
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

data class user(val phone_number:String, val name:String){

}


class signup_fragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup_fragment, container, false)

        view.findViewById<TextView>(R.id.loginRedirect).setOnClickListener {
            view.findNavController().navigate(R.id.action_signup_fragment2_to_organizationSignIn)
        }

        view.findViewById<Button>(R.id.signUpButton).setOnClickListener {
            OrganizationSignUp(view)
        }

        if(!Places.isInitialized()) Places.initialize(this.requireContext(),"AIzaSyB52lDIiUpQD-2i07Ev9l1Ne8BRpIS6qOE")
        view.findViewById<EditText>(R.id.signupLocation).focusable=View.NOT_FOCUSABLE

        view.findViewById<EditText>(R.id.signupLocation).setOnClickListener{

            val fieldlist = listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldlist).build(this.requireContext())
            startActivityForResult(intent, 100)


        }

        return view
    }

    fun OrganizationSignUp(view : View)
    {
        val nameField=view.findViewById<EditText>(R.id.signupName)
        val phoneNumberField=view.findViewById<EditText>(R.id.signupPhoneNumber)
        val emailField=view.findViewById<EditText>(R.id.signupEmail)
        val passwordField = view.findViewById<EditText>(R.id.signupPassword)

        if(nameField.text.toString().trim().isEmpty())
        {
            nameField.error="Enter your name"
            nameField.requestFocus()
            return
        }

        if(phoneNumberField.text.toString().trim().isEmpty() || phoneNumberField.text.toString().trim().length!=10)
        {
            phoneNumberField.error="Enter a valid phone number"
            phoneNumberField.requestFocus()
            return
        }

        if(emailField.text.toString().trim().isEmpty())
        {
            emailField.error="Enter your email"
            emailField.requestFocus()
            return
        }

        if(passwordField.text.toString().trim().isEmpty())
        {
            passwordField.error="Enter a password"
            passwordField.requestFocus()
            return
        }

        mAuth = FirebaseAuth.getInstance()
        mAuth.createUserWithEmailAndPassword(emailField.text.toString().trim(), passwordField.text.toString().trim())
            .addOnCompleteListener {    task->
                if(task.isSuccessful)
                {
                    val user = Firebase.auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nameField.text.toString().trim()).build()
                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                //enter into firestore
                                val db = Firebase.firestore
                                val data = hashMapOf(
                                    "name" to nameField.text.toString().trim(),
                                    "email" to emailField.text.toString().trim(),
                                    "phone number" to phoneNumberField.text.toString().trim(),
                                    "Location" to null,
                                    "address" to null,
                                    "id" to user.uid
                                )

                                db.collection("NGO Addresses").document(user.uid).set(data).addOnSuccessListener {

                                    Toast.makeText(this.requireContext(), "Registration successfull!!", Toast.LENGTH_SHORT).show()
                                    val OrganizationIntent = Intent(this.requireContext(), Organization::class.java)
                                    OrganizationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(OrganizationIntent)

                                }.addOnFailureListener {
                                    Toast.makeText(this.requireContext(), "Unable to add details to database", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else
                            {
                                Toast.makeText(this.requireContext(), "Unable to set user details", Toast.LENGTH_SHORT).show()
                            }
                        }
                }

                else
                {
                    emailField.error="Email already exists"
                    emailField.requestFocus()
                }
            }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == RESULT_OK)
        {

            val place = data?.let { Autocomplete.getPlaceFromIntent(it) }

            if (place != null) {
                view?.findViewById<EditText>(R.id.signupLocation)?.setText(place.address)
            }
            if (place != null) {
                println(place.latLng)
            }
        }

        else if(resultCode == RESULT_ERROR)
        {
            val status = data?.let { Autocomplete.getStatusFromIntent(it) }
            if (status != null) {
                Toast.makeText(this.requireContext(), status.statusMessage, Toast.LENGTH_SHORT).show()
            }

        }
    }

}