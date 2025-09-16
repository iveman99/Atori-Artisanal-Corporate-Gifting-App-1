package com.example.buynow

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.buynow.data.model.Product
import com.example.buynow.presentation.adapter.ProductAdapter
import com.example.buynow.presentation.adapter.SaleProductAdapter
import com.google.firebase.firestore.FirebaseFirestore

class digital : AppCompatActivity() {

    private lateinit var newRecView: RecyclerView
    private lateinit var saleRecView: RecyclerView
    private lateinit var newProduct: ArrayList<Product>
    private lateinit var saleProduct: ArrayList<Product>

    private lateinit var newProductAdapter: ProductAdapter
    private lateinit var saleProductAdapter: SaleProductAdapter

    private lateinit var animationView: LottieAnimationView
    private lateinit var newLayout: LinearLayout
    private lateinit var saleLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_digital)

        // Full-screen layout
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Initialize lists
        newProduct = arrayListOf()
        saleProduct = arrayListOf()

        // Initialize views
        newRecView = findViewById(R.id.newRecView)
        saleRecView = findViewById(R.id.saleRecView)
        newLayout = findViewById(R.id.newLayout)
        saleLayout = findViewById(R.id.saleLayout)
        animationView = findViewById(R.id.animationView)

        // Hide layout and show loading
        hideLayout()

        // Set up RecyclerViews
        newRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        newRecView.setHasFixedSize(true)
        newProductAdapter = ProductAdapter(newProduct, this)
        newRecView.adapter = newProductAdapter

        saleRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        saleRecView.setHasFixedSize(true)
        saleProductAdapter = SaleProductAdapter(saleProduct, this)
        saleRecView.adapter = saleProductAdapter

        // Fetch products from Firestore
        fetchNewProductsFromFirestore()
        fetchSaleProductsFromFirestore()
    }

    private fun hideLayout() {
        animationView.playAnimation()
        animationView.loop(true)
        newLayout.visibility = View.GONE
        saleLayout.visibility = View.GONE
        animationView.visibility = View.VISIBLE
    }

    private fun showLayout() {
        animationView.pauseAnimation()
        animationView.visibility = View.GONE
        newLayout.visibility = View.VISIBLE
        saleLayout.visibility = View.VISIBLE
    }

    private fun fetchNewProductsFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Product")
            .whereEqualTo("productCategory", "dc")
            .get()
            .addOnSuccessListener { documents ->
                newProduct.clear()
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    newProduct.add(product)
                }
                newProductAdapter.notifyDataSetChanged()
                Log.d("Firestore", "Loaded ${newProduct.size} Digital Clocks")
                showLayout()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching Digital Clocks", e)
                Toast.makeText(this, "Failed to load Digital Clock products", Toast.LENGTH_SHORT).show()
                showLayout()
            }
    }

    private fun fetchSaleProductsFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Product")
            .whereEqualTo("productCategory", "digital clock")
            .get()
            .addOnSuccessListener { documents ->
                saleProduct.clear()
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    saleProduct.add(product)
                }
                saleProductAdapter.notifyDataSetChanged()
                Log.d("Firestore", "Loaded ${saleProduct.size} DC products")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching DC products", e)
                Toast.makeText(this, "Failed to load DC products", Toast.LENGTH_SHORT).show()
            }
    }
}
