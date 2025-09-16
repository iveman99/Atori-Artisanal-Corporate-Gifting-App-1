package com.example.buynow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.buynow.data.model.Notification
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(private val notificationList: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.textTitle)
        val messageText: TextView = itemView.findViewById(R.id.textMessage)
        val timeText: TextView = itemView.findViewById(R.id.textTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.titleText.text = notification.title
        holder.messageText.text = notification.message
        holder.timeText.text = notification.timestamp.toDateFormatted()
    }

    override fun getItemCount(): Int = notificationList.size
}

// ✅ Extension function for formatting timestamp
private fun Timestamp?.toDateFormatted(): String {
    return this?.toDate()?.let {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(it)
    } ?: "Unknown Time"
}
