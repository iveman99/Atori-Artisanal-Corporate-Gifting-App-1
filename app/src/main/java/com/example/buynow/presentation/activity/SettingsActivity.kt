package com.example.buynow.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buynow.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SettingsActivity : AppCompatActivity() {

    private lateinit var backIv: ImageView
    private lateinit var nameEt: EditText
    private lateinit var emailEt: EditText
    private lateinit var updateProfileBtn: Button
    private lateinit var supportBtn: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // UI Elements
        backIv = findViewById(R.id.backIv)
        nameEt = findViewById(R.id.nameEt)
        emailEt = findViewById(R.id.emailEt)
        updateProfileBtn = findViewById(R.id.updateProfileBtn)
        supportBtn = findViewById(R.id.supportBtn)

        backIv.setOnClickListener {
            finish()
        }

        updateProfileBtn.setOnClickListener {
            val fullName = nameEt.text.toString().trim()
            val email = emailEt.text.toString().trim()

            val userId = auth.currentUser?.uid

            if (userId != null) {
                val updatedUser = hashMapOf(
                    "fullName" to fullName,
                    "email" to email
                )

                db.collection("Users").document(userId)
                    .set(updatedUser, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

        supportBtn.setOnClickListener {
            sendSupportEmail()
        }
    }

    private fun sendSupportEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:atorigifts@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, "Support Request")
            putExtra(Intent.EXTRA_TEXT, "Dear Support Team,\n\nI need help with...")
        }
        try {
            startActivity(emailIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "No Email App Installed!", Toast.LENGTH_SHORT).show()
        }
    }
}
