package io.realworld.ecoconnect

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth

class OrganizationSignIn : Fragment() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_organization_sign_in, container, false)

        view.findViewById<TextView>(R.id.orgSignUpRedirect).setOnClickListener {
            view.findNavController().navigate(R.id.action_organizationSignIn_to_signup_fragment2)
        }

        view.findViewById<Button>(R.id.orgLoginButton).setOnClickListener {
            orgLogin(view)
        }

        return view
    }


    fun orgLogin(view:View)
    {
        val emailField=view.findViewById<EditText>(R.id.loginEmail)
        val passwordField = view.findViewById<EditText>(R.id.loginPassword)

        if(emailField.text.toString().trim().isEmpty())
        {
            emailField.error="Enter your email"
            emailField.requestFocus()
            return
        }

        if(passwordField.text.toString().trim().isEmpty())
        {
            passwordField.error="Enter your password"
            passwordField.requestFocus()
            return
        }

        mAuth= FirebaseAuth.getInstance()
        mAuth.signInWithEmailAndPassword(emailField.text.toString().trim(), passwordField.text.toString().trim())
            .addOnCompleteListener {    task ->
                if(task.isSuccessful)
                {
                    val OrganizationIntent = Intent(this.requireContext(), Organization::class.java)
                    OrganizationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(OrganizationIntent)
                }

                else
                {
                    Toast.makeText(this.requireContext(), "Please check your credentials", Toast.LENGTH_SHORT).show()
                }
            }
    }
}