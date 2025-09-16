package com.example.buynow

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.net.Uri
import android.widget.Button


class aboutus : AppCompatActivity() {



        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_about_us) // Ensure this matches your XML file name

            // Call Karishma
            val karishmaPhone: Button = findViewById(R.id.karishmaPhone)
            karishmaPhone.setOnClickListener {
                dialPhoneNumber("9960248154")
            }

            // Call Sakshi
            val sakshiPhone: Button = findViewById(R.id.sakshiPhone)
            sakshiPhone.setOnClickListener {
                dialPhoneNumber("8766688629")
            }

            // Send Email
            val emailButton: Button = findViewById(R.id.emailButton)
            emailButton.setOnClickListener {
                sendEmail("atorigifts@gmail.com")
            }

            // Open Website
            val websiteButton: Button = findViewById(R.id.websiteButton)
            websiteButton.setOnClickListener {
                openWebsite("https://www.atori.in")
            }
        }

        // Function to open dialer with phone number
        private fun dialPhoneNumber(phoneNumber: String) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }

        // Function to send email
        private fun sendEmail(email: String) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$email")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry about Corporate Gifting")
            startActivity(intent)
        }

        // Function to open website
        private fun openWebsite(url: String) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }
