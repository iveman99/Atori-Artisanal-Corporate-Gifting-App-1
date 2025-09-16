package com.example.buynow.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buynow.data.model.Category
import com.example.buynow.R

class CategoryAdapter(
    private val ctx: Context,
    private val categoryList: ArrayList<Category>,
    private val onCategoryClick: (Category) -> Unit  // ✅ Click Listener
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val categoryView = LayoutInflater.from(parent.context).inflate(R.layout.category_single, parent, false)
        return ViewHolder(categoryView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Category = categoryList[position]
        holder.categoryName.text = item.Name

        Glide.with(ctx)
            .load(item.Image)
            .into(holder.categoryImage)

        // ✅ Handle item click
        holder.itemView.setOnClickListener {
            onCategoryClick(item)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImage: ImageView = itemView.findViewById(R.id.categoryImage_CateSingle)
        val categoryName: TextView = itemView.findViewById(R.id.categoryName_CateSingle)
    }
}

