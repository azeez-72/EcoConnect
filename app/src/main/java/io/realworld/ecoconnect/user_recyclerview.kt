package io.realworld.ecoconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserModel(val id:String, val name:String , val ngoId:String, val weight:String, val status:String)

class User_RecyclerView(private val docs:MutableList<UserModel>) : RecyclerView.Adapter<User_RecyclerView.ViewHolder>()
{
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.pickupUsername)
        val address = itemView.findViewById<TextView>(R.id.pickupAddress)
        val phone = itemView.findViewById<TextView>(R.id.pickupPhone)
        val weight = itemView.findViewById<TextView>(R.id.pickupWeight)
        val button = itemView.findViewById<TextView>(R.id.pickupReject)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.pickup_card_view, parent, false)
        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        return docs.size
    }

    override fun onBindViewHolder(holder: User_RecyclerView.ViewHolder, position: Int) {
        holder.name.text = "NGO: ${docs[position].name}"
        holder.phone.text = "Weight: ${docs[position].weight} kg"
        holder.address.text = "Status: ${docs[position].status}"
        holder.weight.visibility=View.GONE

        holder.button.text="Cancel"
        if(docs[position].status=="cancelled" || docs[position].status=="cancelled by NGO") holder.button.visibility=View.GONE
        holder.button.setOnClickListener {
            val db=Firebase.firestore
            db.collection("NGO Addresses").document(docs[position].ngoId)
                .collection("Pickups").document(docs[position].id).delete()
                .addOnSuccessListener {
                    db.collection("users").document(FirebaseAuth.getInstance().currentUser.uid)
                        .collection("Pickups").document(docs[position].id)
                        .update("status","cancelled")
                        .addOnSuccessListener {
                            holder.address.text = "Status: cancelled"
                            holder.button.visibility=View.GONE
                        }
                }
        }

    }
}