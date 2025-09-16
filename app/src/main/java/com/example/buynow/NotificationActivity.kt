package com.example.buynow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buynow.data.model.Notification
import com.example.buynow.databinding.ActivityNotificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val notifications = mutableListOf<Notification>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = NotificationAdapter(notifications)
        binding.recyclerViewNotifications.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNotifications.adapter = adapter

        val userId = auth.currentUser?.uid ?: return

        db.collection("Users")
            .document(userId)
            .collection("Notifications") // ✅ Fixed casing
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val list = result.map {
                    Notification(
                        title = it.getString("title") ?: "",
                        message = it.getString("message") ?: "",
                        timestamp = it.getTimestamp("timestamp")
                    )
                }
                notifications.clear()
                notifications.addAll(list)
                adapter.notifyDataSetChanged()
            }
    }
}
