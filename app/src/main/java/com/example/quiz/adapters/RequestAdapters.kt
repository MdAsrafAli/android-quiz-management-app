package com.example.quiz.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.models.UserData
import com.google.firebase.database.*

class RequestAdapters(
    private val userList: MutableList<UserData>,
    private val userID: String,
    private val emptyTextView: TextView
) : RecyclerView.Adapter<RequestAdapters.UserViewHolder>() {

    private val placeholder = UserData("Empty List", "", "")

    init {
        if (userList.isEmpty()) {
            userList.add(placeholder)
        }
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        val emailTextView: TextView = itemView.findViewById(R.id.textViewEmail)
        val phoneTextView: TextView = itemView.findViewById(R.id.textViewPhone)
        val deleteButton: Button = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val userData = userList[position]

        if (userData == placeholder) {
            holder.nameTextView.text = "No Requests"
            holder.emailTextView.visibility = View.GONE
            holder.phoneTextView.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        } else {
            holder.nameTextView.text = userData.name
            holder.emailTextView.text = userData.email
            holder.phoneTextView.text = userData.phone
            holder.emailTextView.visibility = View.VISIBLE
            holder.phoneTextView.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE

            holder.deleteButton.setOnClickListener {
                holder.deleteButton.visibility = View.GONE
                val currentPosition = holder.adapterPosition
                if (currentPosition == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }

                val phone = userData.phone
                if (phone.isNullOrEmpty()) {
                    Log.e("DeleteFailed", "Phone number is null or empty")
                    return@setOnClickListener
                }

                Log.d("DeleteDebug", "Deleting phone: $phone under userID: $userID")

                val databaseRef = FirebaseDatabase.getInstance().reference
                var query = databaseRef.child("requests").child(userID)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var matchFound = false
                            for (requestSnapshot in snapshot.children) {
                                val value = requestSnapshot.getValue(String::class.java)
                                if (value == phone) {
                                    matchFound = true
                                    requestSnapshot.ref.removeValue()
                                        .addOnSuccessListener {
                                            if (currentPosition < userList.size) {
                                                userList.removeAt(currentPosition)
                                                notifyItemRemoved(currentPosition)
                                                if (userList.isEmpty()) {
                                                    userList.add(placeholder)
                                                    notifyItemInserted(0)
                                                }
                                                updateEmptyView()
                                            }
                                            Toast.makeText(
                                                holder.itemView.context,
                                                "Request deleted",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.e("DeleteFailed", "Failed to delete request: $exception")
                                        }
                                    break
                                }
                            }
                            if (!matchFound) {
                                Log.e("DeleteFailed", "No matching request found to delete")
                            }
                        } else {
                            Log.e("DeleteFailed", "No matching request found to delete")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("DeleteFailed", "Failed to find request to delete: ${error.message}")
                    }
                })
                query = databaseRef.child("requests").child(phone)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var matchFound = false
                            for (requestSnapshot in snapshot.children) {
                                val value = requestSnapshot.getValue(String::class.java)
                                if (value == userID ) {
                                    matchFound = true
                                    requestSnapshot.ref.removeValue()
                                        .addOnSuccessListener {
                                            if (currentPosition < userList.size) {
                                                userList.removeAt(currentPosition)
                                                notifyItemRemoved(currentPosition)
                                                if (userList.isEmpty()) {
                                                    userList.add(placeholder)
                                                    notifyItemInserted(0)
                                                }
                                                updateEmptyView()
                                            }
                                            Toast.makeText(
                                                holder.itemView.context,
                                                "Request deleted",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.e("DeleteFailed", "Failed to delete request: $exception")
                                        }
                                    break
                                }
                            }
                            if (!matchFound) {
                                Log.e("DeleteFailed", "No matching request found to delete")
                            }
                        } else {
                            Log.e("DeleteFailed", "No matching request found to delete")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("DeleteFailed", "Failed to find request to delete: ${error.message}")
                    }
                })
            }
        }
    }

    fun updateEmptyView() {
        if (userList.isEmpty() || userList.size == 1 && userList[0] == placeholder) {
            emptyTextView.visibility = View.VISIBLE
        } else {
            emptyTextView.visibility = View.GONE
        }
    }
}
