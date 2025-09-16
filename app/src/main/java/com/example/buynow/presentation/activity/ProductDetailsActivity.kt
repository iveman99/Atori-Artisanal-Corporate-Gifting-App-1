package com.example.buynow.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buynow.R
import com.example.buynow.data.local.room.CartViewModel
import com.example.buynow.data.local.room.ProductEntity
import com.example.buynow.data.model.Product
import com.example.buynow.presentation.adapter.ProductAdapter
import com.example.buynow.utils.DefaultCard.GetDefCard
import com.example.buynow.utils.Extensions.cardXXGen
import com.example.buynow.utils.Extensions.toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var product: Product
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recommendedProducts: ArrayList<Product>
    private lateinit var productPrice: TextView
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productBrand: TextView
    private lateinit var productDesc: TextView
    private lateinit var productRating: RatingBar
    private lateinit var ratingText: TextView
    private lateinit var recomRecyclerView: RecyclerView

    private lateinit var cardNumber: String
    private var quantity: Int = 1

    private lateinit var cartViewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val productJson = intent.getStringExtra("product")
        if (productJson == null) {
            toast("Invalid product data")
            finish()
            return
        }

        product = Gson().fromJson(productJson, Product::class.java)

        initViews()
        setupListeners()

        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        cardNumber = GetDefCard()
        findViewById<TextView>(R.id.cardNumberProduct_Details).text =
            if (cardNumber.isEmpty()) "You Have No Cards" else cardXXGen(cardNumber)

        recommendedProducts = arrayListOf()
        productAdapter = ProductAdapter(recommendedProducts, this)
        recomRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recomRecyclerView.adapter = productAdapter

        setProductData()
        loadRecommendedFromFirestoreOrFallback()

        val addToCartBtn = findViewById<Button>(R.id.addToCart_ProductDetailsPage)
        addToCartBtn.setOnClickListener {
            val productEntity = ProductEntity(
                name = product.productName ?: "",
                qua = quantity.toInt(),
                price = product.productPrice,
                pId = product.productId?.toString() ?: "",
                Image = product.productImage ?: ""
            )

            cartViewModel.insert(productEntity)
            Toast.makeText(this, "${product.productName} added to bag", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews() {
        productImage = findViewById(R.id.productImage_ProductDetailsPage)
        productName = findViewById(R.id.productName_ProductDetailsPage)
        productPrice = findViewById(R.id.productPrice_ProductDetailsPage)
        productBrand = findViewById(R.id.productBrand_ProductDetailsPage)
        productDesc = findViewById(R.id.productDes_ProductDetailsPage)
        productRating = findViewById(R.id.productRating_singleProduct)
        ratingText = findViewById(R.id.RatingProductDetails)
        recomRecyclerView = findViewById(R.id.RecomRecView_ProductDetailsPage)
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.backIv_ProfileFrag).setOnClickListener { onBackPressed() }
        findViewById<LinearLayout>(R.id.shippingAddress_productDetailsPage).setOnClickListener {
            startActivity(Intent(this, PaymentMethodActivity::class.java))
        }
    }

    private fun setProductData() {
        Glide.with(this).load(product.productImage).into(productImage)
        productName.text = product.productName
        productBrand.text = product.productBrand
        productDesc.text = product.productDes
        productPrice.text = product.productPrice
        productRating.rating = product.productRating
        ratingText.text = "${product.productRating} Rating on this Product."
    }

    private fun loadRecommendedFromFirestoreOrFallback() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Product")
            .whereEqualTo("productCategory", product.productCategory)
            .get()
            .addOnSuccessListener { documents ->
                recommendedProducts.clear()
                for (doc in documents) {
                    val prod = doc.toObject(Product::class.java)
                    // Skip current product
                    if (prod.productId != product.productId) {
                        recommendedProducts.add(prod)
                    }
                }

                if (recommendedProducts.isEmpty()) {
                    loadRecommendedFromJson()
                } else {
                    productAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to fetch recommended, loading from JSON")
                loadRecommendedFromJson()
            }
    }

    private fun loadRecommendedFromJson() {
        val jsonFile = getJsonFileName(product.productCategory)
        val jsonString = getJsonData(this, jsonFile)

        if (!jsonString.isNullOrEmpty()) {
            val productList: List<Product> = Gson().fromJson(jsonString, object : TypeToken<List<Product>>() {}.type)
            recommendedProducts.clear()
            recommendedProducts.addAll(productList.filter { it.productId != product.productId })
            productAdapter.notifyDataSetChanged()
        }
    }

    private fun getJsonData(context: Context, fileName: String): String? {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ex: IOException) {
            Log.e("JSON_ERROR", "Error reading file: $fileName", ex)
            null
        }
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
