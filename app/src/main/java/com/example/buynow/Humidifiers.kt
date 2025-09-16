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

class Humidifiers : AppCompatActivity() {

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
        setContentView(R.layout.activity_humidifiers)

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
        fetchHumidifierProducts()
        fetchHumiProducts()
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

    private fun fetchHumidifierProducts() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Product")
            .whereEqualTo("productCategory", "humidifier")
            .get()
            .addOnSuccessListener { documents ->
                newProduct.clear()
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    newProduct.add(product)
                }
                newProductAdapter.notifyDataSetChanged()
                Log.d("Firestore", "Loaded ${newProduct.size} humidifier products")
                showLayout()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching humidifier products", e)
                Toast.makeText(this, "Failed to load humidifier products", Toast.LENGTH_SHORT).show()
                showLayout()
            }
    }

    private fun fetchHumiProducts() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Product")
            .whereEqualTo("productCategory", "humi")
            .get()
            .addOnSuccessListener { documents ->
                saleProduct.clear()
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    saleProduct.add(product)
                }
                saleProductAdapter.notifyDataSetChanged()
                Log.d("Firestore", "Loaded ${saleProduct.size} humi products")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching humi products", e)
                Toast.makeText(this, "Failed to load humi products", Toast.LENGTH_SHORT).show()
            }
    }
}
