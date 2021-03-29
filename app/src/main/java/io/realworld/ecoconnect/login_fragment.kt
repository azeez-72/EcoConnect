package io.realworld.ecoconnect

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import java.util.concurrent.TimeUnit

class login_fragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var phone_number: String
    private lateinit var refUsers: DatabaseReference
    private lateinit var storedVerificationId : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_login_fragment, container, false)
        view.findViewById<TextView>(R.id.signupRedirect).setOnClickListener {
            view.findNavController().navigate(R.id.action_login_fragment2_to_signup_fragment2)
        }

        view.findViewById<Button>(R.id.loginButton).setOnClickListener {

            val phone_number = view.findViewById<EditText>(R.id.loginPhoneNumber)
            this.phone_number=phone_number.text.toString().trim()
            if(this.phone_number.isEmpty() || this.phone_number.length!=10) {
                phone_number.error = "Enter a valid Phone number"
                phone_number.requestFocus()
            }

            else
                loginUser(phone_number)
        }



        view.findViewById<Button>(R.id.loginOTPButton).setOnClickListener{
            val otp = view.findViewById<EditText>(R.id.loginOTP)
            if(otp.text.toString().trim().isEmpty())
            {
                otp.error="Enter OTP"
                otp.requestFocus()
            }
            else{
                val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp.text.toString().trim())
                signInWithPhoneAuthCredential(credential)
            }
        }

        return view
    }



    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(p0)

        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Toast.makeText(activity?.applicationContext, "Please check the entered OTP", Toast.LENGTH_SHORT).show()
            println(p0.message)
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)

            storedVerificationId = p0
            view?.findViewById<Button>(R.id.loginOTPButton)?.visibility = View.VISIBLE
            view?.findViewById<Button>(R.id.loginButton)?.visibility = View.GONE
            view?.findViewById<EditText>(R.id.loginOTP)?.visibility = View.VISIBLE

            Toast.makeText(activity?.applicationContext, "Please enter the OTP", Toast.LENGTH_SHORT).show()
        }

    }




    private fun loginUser(phone_number : EditText){

        refUsers = FirebaseDatabase.getInstance().getReference("Users")
        val activity = this
        refUsers.orderByChild("phone_number").equalTo(this.phone_number).addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val phone_number_text = activity.phone_number
                if(snapshot.getValue() != null){
                    mAuth = FirebaseAuth.getInstance()
                    println("+91$phone_number_text")
                    val options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91$phone_number_text")       // Phone number to verify
                        .setTimeout(10L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity.requireActivity())                 // Activity (for callback binding)
                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                        .build()


                    PhoneAuthProvider.verifyPhoneNumber(options)

                }

                else{
                    phone_number.error="Unregistered Phone Number"
                    phone_number.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity.context,"Unable to connect to Firebase",Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth= FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")

                    val dashboardIntent = Intent(activity, MainActivity::class.java)
                    dashboardIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(dashboardIntent)
                }


                else{
                    Toast.makeText(this.context, "Unsuccessful :(", Toast.LENGTH_SHORT).show()
                }
            }

        }
}
