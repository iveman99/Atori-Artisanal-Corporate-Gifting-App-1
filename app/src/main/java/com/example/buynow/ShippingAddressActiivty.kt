package com.example.buynow

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.buynow.presentation.activity.PaymentMethodActivity

class ShippingAddressActiivty : AppCompatActivity() {

    private lateinit var totalAmountText: TextView
    private lateinit var productNameText: TextView
    private lateinit var fullNameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var stateEditText: EditText
    private lateinit var zipEditText: EditText
    private lateinit var continueToPaymentButton: Button

    private var totalAmount: Int = 0
    private var productNames: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipping_address_actiivty)

        totalAmountText = findViewById(R.id.totalAmountText)
        productNameText = findViewById(R.id.productNameText)
        fullNameEditText = findViewById(R.id.fullNameEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        addressEditText = findViewById(R.id.addressEditText)
        cityEditText = findViewById(R.id.cityEditText)
        stateEditText = findViewById(R.id.stateEditText)
        zipEditText = findViewById(R.id.zipEditText)
        continueToPaymentButton = findViewById(R.id.continueToPaymentButton)

        totalAmount = intent.getIntExtra("TOTAL_AMOUNT", 0)
        productNames = intent.getStringExtra("PRODUCT_NAMES") ?: "Unknown Product"

        totalAmountText.text = "₹$totalAmount"
        productNameText.text = productNames

        continueToPaymentButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            val city = cityEditText.text.toString().trim()
            val state = stateEditText.text.toString().trim()
            val zip = zipEditText.text.toString().trim()

            if (fullName.isEmpty() || phone.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty() || zip.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // ✅ ZIP code validation: must be 6 digits and numeric
            if (!zip.matches(Regex("^[0-9]{6}\$"))) {
                Toast.makeText(this, "Enter a valid 6-digit ZIP code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // ✅ Phone validation: must be 10 digits and numeric
            if (!phone.matches(Regex("^[0-9]{10}\$"))) {
                Toast.makeText(this, "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val shippingAddress = "$fullName, $phone\n$address, $city, $state - $zip"

            val intent = Intent(this, PaymentMethodActivity::class.java).apply {
                putExtra("USER_NAME", fullName)
                putExtra("PHONE", phone)
                putExtra("SHIPPING_ADDRESS", shippingAddress)
                putExtra("PRODUCT_NAMES", productNames)
                putExtra("TOTAL_AMOUNT", totalAmount)
            }
            startActivity(intent)
        }
    }
}
