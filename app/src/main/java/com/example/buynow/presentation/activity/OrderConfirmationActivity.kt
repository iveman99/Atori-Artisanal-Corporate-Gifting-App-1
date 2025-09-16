package com.example.buynow.presentation.activity

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.buynow.R
import com.example.buynow.data.local.room.CartViewModel
import com.example.buynow.data.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val userName = intent.getStringExtra("USER_NAME") ?: "User"
        val phone = intent.getStringExtra("PHONE") ?: "N/A"
        val shippingAddress = intent.getStringExtra("SHIPPING_ADDRESS") ?: "Not Available"
        val productNames = intent.getStringExtra("PRODUCT_NAMES") ?: "Unknown Product"
        val totalAmount = intent.getIntExtra("TOTAL_AMOUNT", 0)
        val paymentMethod = intent.getStringExtra("PAYMENT_METHOD") ?: "Cash"

        findViewById<TextView>(R.id.userNameTextView).text = userName
        findViewById<TextView>(R.id.shippingAddressTextView).text = shippingAddress
        findViewById<TextView>(R.id.paymentMethodTextView).text = paymentMethod
        findViewById<TextView>(R.id.totalAmountTextView).text = "₹$totalAmount"
        findViewById<TextView>(R.id.productName).text = productNames.replace(", ", "\n")

        findViewById<Button>(R.id.placeOrderBtn).setOnClickListener {
            placeOrder(userName, phone, shippingAddress, productNames,
                totalAmount.toString(), paymentMethod)
        }
    }

    private fun placeOrder(
        userName: String,
        phone: String,
        shippingAddress: String,
        productNames: String,
        totalAmount: String,
        paymentMethod: String
    ) {
        val order = Order(
            userId = auth.currentUser?.uid ?: "guest",
            userName = userName,
            shippingAddress = shippingAddress,
            productNames = productNames,
            totalAmount =  totalAmount,
            paymentMethod = paymentMethod,
            orderStatus = "Pending"
        )

        val docId = "${shippingAddress}_${System.currentTimeMillis()}"

        db.collection("orders").document(docId).set(order)
            .addOnSuccessListener {
                Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_SHORT).show()
                cartViewModel.deleteAllCart()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to place order!", Toast.LENGTH_SHORT).show()
            }
    }
}
