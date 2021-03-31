package io.realworld.ecoconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Model(val address:String, val name:String, val phone:String, val weight:String)

class RecyclerAdapter(private val docs:MutableList<Model>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>()
{
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.pickupUsername)
        val address = itemView.findViewById<TextView>(R.id.pickupAddress)
        val phone = itemView.findViewById<TextView>(R.id.pickupPhone)
        val weight = itemView.findViewById<TextView>(R.id.pickupWeight)
        val button = itemView.findViewById<TextView>(R.id.pickupReject)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.pickup_card_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = docs[position].name
        holder.phone.text = docs[position].phone
        holder.address.text = docs[position].address
        holder.weight.text = docs[position].weight

        holder.button.setOnClickListener {
            val db = Firebase.firestore
            val user = FirebaseAuth.getInstance().currentUser
            docs.removeAt(position)
            db.collection("Pickups").document(user.uid)
                .update(mapOf(
                    "pickup ids" to docs
                ))
                .addOnSuccessListener {
                    holder.itemView.visibility=View.GONE
                }
        }
    }

    override fun getItemCount(): Int {
        return docs.size
    }

}