package com.example.buynow.data.model
data class Order(
    var userId: String = "",
    var userName: String = "",
    var shippingAddress: String = "",
    var paymentMethod: String = "",
    var productNames: String = "",
    var totalAmount: String = "",
    var orderStatus: String = "Pending",
    var orderTimestamp: Long = System.currentTimeMillis(),
    var orderId: String = "" ,
    val deliveryDate: String? = null
)
