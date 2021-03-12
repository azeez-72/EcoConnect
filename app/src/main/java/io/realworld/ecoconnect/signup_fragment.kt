package io.realworld.ecoconnect

import android.content.ContentValues.TAG
import android.content.Intent
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
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import java.util.concurrent.TimeUnit

data class user(val phone_number:String, val name:String){

}


class signup_fragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var storedVerificationId : String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var name:String
    private lateinit var phone_number: String
    private lateinit var refUsers: DatabaseReference

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup_fragment, container, false)

        view.findViewById<TextView>(R.id.loginRedirect).setOnClickListener {
            view.findNavController().navigate(R.id.action_signup_fragment2_to_login_fragment2)
        }

        view.findViewById<Button>(R.id.signUpButton).setOnClickListener{
            val name=view.findViewById<EditText>(R.id.signupName)
            val phone_number=view.findViewById<EditText>(R.id.signupPhoneNumber)



            if(name.text.toString().trim().isEmpty()) {
                name.error = "Name is required"
                name.requestFocus()
            }

            if(phone_number.text.toString().trim().isEmpty()) {
                phone_number.error = "Phone number is required"
                phone_number.requestFocus()
            }

            if(phone_number.text.toString().trim().length != 10)
            {
                phone_number.error = "Enter a valid phone number"
                phone_number.requestFocus()
            }

            else {
                this.name=name.text.toString().trim()
                this.phone_number=phone_number.text.toString().trim()


                refUsers = FirebaseDatabase.getInstance().getReference("Users")
                val activity = this
                refUsers.orderByChild("phone_number").equalTo(phone_number.text.toString().trim()).addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if(snapshot.getValue() != null){
                            phone_number.error = "Phone Number already exists"
                            phone_number.requestFocus()
                        }

                        else{
                            userSignup(name.text.toString().trim(), phone_number.text.toString().trim())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(activity.requireContext(),"Cannot connect to firebase",Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }


        view.findViewById<Button>(R.id.signuoOTPButton).setOnClickListener{
            val otp=view.findViewById<EditText>(R.id.signupOTP)
            if(otp.text.toString().trim().isEmpty())
            {
                otp.error="Enter the OTP"
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
            view?.findViewById<EditText>(R.id.signupOTP)?.visibility = View.VISIBLE
            view?.findViewById<Button>(R.id.signUpButton)?.visibility = View.GONE
            view?.findViewById<Button>(R.id.signuoOTPButton)?.visibility = View.VISIBLE
            Toast.makeText(activity?.applicationContext, "Please enter the OTP", Toast.LENGTH_SHORT).show()
        }

        }

    private fun userSignup(name: String, phone_number: String){

        mAuth = FirebaseAuth.getInstance()

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber("+91$phone_number")       // Phone number to verify
            .setTimeout(10L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this.requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()


        PhoneAuthProvider.verifyPhoneNumber(options)

    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                val userToAdd = user(phone_number,name)
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().currentUser.uid).setValue(userToAdd)
                        .addOnCompleteListener{task->
                            if(task.isSuccessful) {
                                val dashboardIntent= Intent(activity, MainActivity::class.java)
                                dashboardIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(dashboardIntent)

                            }
                            else{
                                Toast.makeText(activity, "Unsuccesful :(", Toast.LENGTH_SHORT).show()
                            }
                        }

                    // ...
                } else {
                    Toast.makeText(activity?.applicationContext, "Sign In Failed", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(activity?.applicationContext, "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }





}