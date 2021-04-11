package io.realworld.ecoconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Organization : AppCompatActivity() {

    lateinit var mAuth:FirebaseAuth
    lateinit var recycler:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organization)
        mAuth = FirebaseAuth.getInstance()
        val docs = mutableListOf<Model>()

        val user = mAuth.currentUser

        val db = Firebase.firestore
        db.collection("NGO Addresses").document(user.uid).collection("Pickups").get()
            .addOnSuccessListener { documents ->
                for(document in documents)
                {
                    val doc2= document.data
                    docs.add(Model(document.id, doc2["uid"].toString() , doc2["description"].toString(),  doc2["name"].toString(), doc2["phone"].toString(), doc2["weight"].toString()))
                }

                this.recycler = findViewById<RecyclerView>(R.id.recyclerView2)
                findViewById<RecyclerView>(R.id.recyclerView2).layoutManager = LinearLayoutManager(this)
                findViewById<RecyclerView>(R.id.recyclerView2).adapter = RecyclerAdapter(docs)
            }

        findViewById<Button>(R.id.OrgSignout).setOnClickListener {
            Firebase.auth.signOut()
            val loginSignupIntent = Intent(this, LoginSignUpActivity::class.java)
            loginSignupIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginSignupIntent)
        }

    }

}