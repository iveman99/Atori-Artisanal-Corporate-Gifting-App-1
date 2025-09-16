
package com.example.buynow.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.buynow.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    private lateinit var emailEt: EditText
    private lateinit var resetBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Initialize UI components
        emailEt = findViewById(R.id.emailEt)
        resetBtn = findViewById(R.id.resetBtn)
        progressBar = findViewById(R.id.progressBar)

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        resetBtn.setOnClickListener {
            val email = emailEt.text.toString().trim()

            if (email.isEmpty()) {
                emailEt.error = "Email is required"
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            resetBtn.isEnabled = false

            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    progressBar.visibility = View.GONE
                    resetBtn.isEnabled = true
                    Toast.makeText(this, "Reset link sent to $email", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                    resetBtn.isEnabled = true
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
