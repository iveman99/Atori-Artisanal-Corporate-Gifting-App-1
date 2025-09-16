package com.example.buynow.presentation.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buynow.R
import com.example.buynow.data.model.Product
import com.example.buynow.presentation.activity.ProductDetailsActivity
import com.google.gson.Gson

class CoverProductAdapter(
    private val ctx: Context,
    private val coverProductList: MutableList<Product>
) : RecyclerView.Adapter<CoverProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val productView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cover_single, parent, false)
        return ViewHolder(productView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coverPro: Product = coverProductList[position]

        holder.productNoteCover.text = coverPro.productNote
        Glide.with(ctx)
            .load(coverPro.productImage)
            .into(holder.productImage_coverPage)

        holder.productCheck_coverPage.setOnClickListener {
            goDetailsPage(position, coverPro)
        }
    }

    override fun getItemCount(): Int {
        return coverProductList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage_coverPage: ImageView = itemView.findViewById(R.id.productImage_coverPage)
        val productNoteCover: TextView = itemView.findViewById(R.id.productNoteCover)
        val productCheck_coverPage: Button = itemView.findViewById(R.id.productCheck_coverPage)
    }


    private fun goDetailsPage(position: Int, product: Product) {
        Log.d("DEBUG_ADAPTER", "Product Clicked - Index: $position, Category: ${product.productCategory}")

        val intent = Intent(ctx, ProductDetailsActivity::class.java)
        val productJson = Gson().toJson(product)
        intent.putExtra("product", productJson)
        intent.putExtra("ProductFrom", product.productCategory)
        ctx.startActivity(intent)
    }
}
