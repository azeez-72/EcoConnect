package io.realworld.ecoconnect.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import io.realworld.ecoconnect.LoginSignUpActivity
import io.realworld.ecoconnect.R


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        root.findViewById<Button>(R.id.signoutButton).setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            // Google sign out
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            GoogleSignIn.getClient(this.requireActivity(), gso).signOut().addOnCompleteListener(this.requireActivity()){
                task ->
                if(task.isSuccessful) {
                    val loginSignupIntent = Intent(activity, LoginSignUpActivity::class.java)
                    loginSignupIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(loginSignupIntent)
                }

                else
                {
                    Toast.makeText(this.requireContext(), "unable to logout", Toast.LENGTH_SHORT).show()
                }

                }



        }

        return root
    }
}