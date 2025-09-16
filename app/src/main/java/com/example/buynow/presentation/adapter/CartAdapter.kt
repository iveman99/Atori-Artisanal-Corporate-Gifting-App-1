package com.example.buynow.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buynow.R
import com.example.buynow.data.local.room.ProductEntity

class CartAdapter(
    private val context: Context,
    private val listener: CartClickListener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var cartItems: MutableList<ProductEntity> = mutableListOf()

    interface CartClickListener {
        fun onItemDeleteClick(product: ProductEntity)
        fun onItemUpdateClick(product: ProductEntity)
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.cartName)
        private val price: TextView = itemView.findViewById(R.id.cartPrice)
        private val quantityTv: TextView = itemView.findViewById(R.id.quantityTvCart)
        private val cartImage: ImageView = itemView.findViewById(R.id.cartImage)
        private val plusBtn: ImageView = itemView.findViewById(R.id.plusLayout)
        private val minusLayout: LinearLayout = itemView.findViewById(R.id.minusLayout)
        private val deleteBtn: ImageView = itemView.findViewById(R.id.cartMore)

        fun bind(cartItem: ProductEntity, position: Int) {
            val currentItem = cartItems[position]
            val unitPrice = currentItem.price.toIntOrNull() ?: 0
            val quantity = currentItem.qua
            val totalPrice = unitPrice * quantity

            name.text = currentItem.name
            price.text = "₹$totalPrice"
            quantityTv.text = quantity.toString()

            Glide.with(context).load(currentItem.Image).into(cartImage)

            plusBtn.setOnClickListener {
                val newQuantity = currentItem.qua + 1
                val updatedItem = currentItem.copy(qua = newQuantity)
                cartItems[position] = updatedItem

                // Instant UI update
                quantityTv.text = newQuantity.toString()
                price.text = "₹${unitPrice * newQuantity}"

                notifyItemChanged(position)
                listener.onItemUpdateClick(updatedItem)
            }

            minusLayout.setOnClickListener {
                if (currentItem.qua > 1) {
                    val newQuantity = currentItem.qua - 1
                    val updatedItem = currentItem.copy(qua = newQuantity)
                    cartItems[position] = updatedItem

                    quantityTv.text = newQuantity.toString()
                    price.text = "₹${unitPrice * newQuantity}"

                    notifyItemChanged(position)
                    listener.onItemUpdateClick(updatedItem)
                } else {
                    listener.onItemDeleteClick(currentItem)
                }
            }

            deleteBtn.setOnClickListener {
                listener.onItemDeleteClick(currentItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item_single, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position], position)
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateList(newList: List<ProductEntity>) {
        cartItems.clear()
        cartItems.addAll(newList)
        notifyDataSetChanged()
    }
}
