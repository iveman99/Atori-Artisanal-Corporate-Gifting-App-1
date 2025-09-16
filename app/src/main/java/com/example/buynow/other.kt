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

class other : AppCompatActivity() {

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
        setContentView(R.layout.activity_other)

        // Make full-screen
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

        // Setup RecyclerViews
        newRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        newProductAdapter = ProductAdapter(newProduct, this)
        newRecView.adapter = newProductAdapter

        saleRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        saleProductAdapter = SaleProductAdapter(saleProduct, this)
        saleRecView.adapter = saleProductAdapter

        // Load data
        hideLayout()
        fetchOther1Products()
        fetchOther2Products()
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

    private fun fetchOther1Products() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Product")
            .whereEqualTo("productCategory", "other")
            .get()
            .addOnSuccessListener { documents ->
                newProduct.clear()
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    newProduct.add(product)
                }
                newProductAdapter.notifyDataSetChanged()
                Log.d("Firestore", "Loaded ${newProduct.size} 'other' products")
                showLayout()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching 'other' products", e)
                Toast.makeText(this, "Failed to load products", Toast.LENGTH_SHORT).show()
                showLayout()
            }
    }

    private fun fetchOther2Products() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Product")
            .whereEqualTo("productCategory", "other1")
            .get()
            .addOnSuccessListener { documents ->
                saleProduct.clear()
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    saleProduct.add(product)
                }
                saleProductAdapter.notifyDataSetChanged()
                Log.d("Firestore", "Loaded ${saleProduct.size} 'other2' products")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching 'other2' products", e)
                Toast.makeText(this, "Failed to load products", Toast.LENGTH_SHORT).show()
            }
    }
}
