package com.example.buynow.presentation.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buynow.R
import com.example.buynow.data.model.Product
import com.example.buynow.presentation.activity.ProductDetailsActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SaleProductAdapter(private val saleProductList: MutableList<Product>, private val context: Context) :
    RecyclerView.Adapter<SaleProductAdapter.ViewHolder>() {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("Favorites", Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val productView = LayoutInflater.from(parent.context).inflate(R.layout.single_product, parent, false)
        return ViewHolder(productView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product: Product = saleProductList[position]

        holder.productBrandName.text = product.productBrand
        holder.productName.text = product.productName
        holder.productRating.rating = product.productRating
        holder.productPrice.text = product.productPrice

        Glide.with(context)
            .load(product.productImage)
            .placeholder(R.drawable.bn)
            .into(holder.productImage)

        holder.discountText.text = if (product.productHave) product.productDisCount.toString() else "New"
        holder.discountLayout.visibility = View.VISIBLE

        updateFavoriteUI(holder.favButton, product)

        holder.itemView.setOnClickListener {
            goToDetailsPage(position, product) // ✅ Fixed parameter order
        }

        holder.favButton.setOnClickListener {
            toggleFavorite(product, holder.favButton)
        }
    }

    override fun getItemCount(): Int {
        return saleProductList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage_singleProduct)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice_singleProduct)
        val productRating: RatingBar = itemView.findViewById(R.id.productRating_singleProduct)
        val productBrandName: TextView = itemView.findViewById(R.id.productBrandName_singleProduct)
        val discountText: TextView = itemView.findViewById(R.id.discountTv_singleProduct)
        val productName: TextView = itemView.findViewById(R.id.productName_singleProduct)
        val discountLayout: LinearLayout = itemView.findViewById(R.id.discount_singleProduct)
        val favButton: ImageView = itemView.findViewById(R.id.productAddToFav_singleProduct)
    }

    private fun isFavorite(product: Product): Boolean {
        val favoritesJson = sharedPreferences.getString("favList", "[]") ?: "[]"
        val type = object : TypeToken<ArrayList<Product>>() {}.type
        val favoritesList: ArrayList<Product> = Gson().fromJson(favoritesJson, type)
        return favoritesList.any { it.productName == product.productName }
    }

    private fun updateFavoriteUI(favButton: ImageView, product: Product) {
        val favoriteIcon = if (isFavorite(product)) R.drawable.ic_favorite_red else R.drawable.ic_favorite_border
        favButton.setImageResource(favoriteIcon)
    }

    private fun toggleFavorite(product: Product, favButton: ImageView) {
        val editor = sharedPreferences.edit()
        val gson = Gson()

        val favoritesJson = sharedPreferences.getString("favList", "[]") ?: "[]"
        val type = object : TypeToken<ArrayList<Product>>() {}.type
        val favoritesList: ArrayList<Product> = gson.fromJson(favoritesJson, type)

        if (isFavorite(product)) {
            favoritesList.removeAll { it.productName == product.productName }
            Toast.makeText(context, "${product.productName} removed from Favorites", Toast.LENGTH_SHORT).show()
            favButton.setImageResource(R.drawable.ic_favorite_border)
        } else {
            favoritesList.add(product)
            Toast.makeText(context, "${product.productName} added to Favorites", Toast.LENGTH_SHORT).show()
            favButton.setImageResource(R.drawable.ic_favorite_red)
        }

        editor.putString("favList", gson.toJson(favoritesList))
        editor.apply()

        updateFavoriteUI(favButton, product)
    }

    private fun goToDetailsPage(position: Int, product: Product) {
        Log.d("DEBUG_ADAPTER", "Product Clicked - Index: $position, Category: ${product.productCategory}")

        val intent = Intent(context, ProductDetailsActivity::class.java)
        val productJson = Gson().toJson(product)
        intent.putExtra("product", productJson)
        intent.putExtra("ProductFrom", product.productCategory)
        context.startActivity(intent)
    }
}
