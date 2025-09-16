package com.example.buynow.presentation.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.buynow.R
import com.example.buynow.data.model.Product
import com.example.buynow.presentation.adapter.ProductAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class CategoryProductFragment : Fragment() {

    class CategoryProductFragment : Fragment() {

        private lateinit var recyclerView: RecyclerView
        private lateinit var adapter: ProductAdapter
        private val productList = mutableListOf<Product>()

        private var category: String? = null

        @SuppressLint("MissingInflatedId")
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_category_product, container, false)

            recyclerView = view.findViewById(R.id.categoryRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter = ProductAdapter(productList, requireContext())
            recyclerView.adapter = adapter

            category = arguments?.getString("categoryName")?.lowercase()
            category?.let { loadProducts(it) }

            return view
        }

        private fun loadProducts(category: String) {
            val db = FirebaseFirestore.getInstance()

            db.collection("Product")
                .whereEqualTo("productCategory", category)
                .get()
                .addOnSuccessListener { documents ->
                    productList.clear()
                    if (documents.isEmpty) {
                        loadFromJson(category)
                    } else {
                        for (doc in documents) {
                            val product = doc.toObject(Product::class.java)
                            productList.add(product)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener {
                    loadFromJson(category)
                }
        }

        private fun loadFromJson(category: String) {
            val fileName = getJsonFileName(category)
            val jsonString =
                requireContext().assets.open(fileName).bufferedReader().use { it.readText() }
            val gson = Gson()
            val type = object : TypeToken<List<Product>>() {}.type
            val products: List<Product> = gson.fromJson(jsonString, type)
            productList.clear()
            productList.addAll(products)
            adapter.notifyDataSetChanged()
        }

        private fun getJsonFileName(category: String): String {
            return when (category.lowercase()) {
                "cover" -> "CoverProducts.json"
                "new" -> "NewProducts.json"
                "digital clocks", "digitalclock", "digitalclocks" -> "DigitalClock.json"
                "dc" -> "Dc.json"
                "ecofriendly1" -> "EcoFriendly1.json"
                "flasks" -> "flasks.json"
                "humi" -> "Humi.json"
                "table lamp2" -> "TableLamp2.json"
                "torch1" -> "Torch2.json"
                "other2" -> "other2.json"
                "humidifier", "humidifiers" -> "Humidifiers.json"
                "mugs and flasks", "mugs" -> "Mugs.json"
                "table lamp" -> "TableLamp1.json"
                "torches" -> "Torch1.json"
                "ecofriendly" -> "Ecofriendly.json"
                "hamper" -> "Hamper.json"
                "hamper1" -> "Hamper1.json"
                "other" -> "other1.json"
                else -> "NewProducts.json"
            }
        }
    }
}