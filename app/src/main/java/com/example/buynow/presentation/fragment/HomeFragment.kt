package com.example.buynow.presentation.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buynow.presentation.adapter.CoverProductAdapter
import com.example.buynow.presentation.adapter.ProductAdapter
import com.example.buynow.presentation.adapter.SaleProductAdapter
import com.example.buynow.data.model.Product
import com.example.buynow.R
import com.example.buynow.ShowCase
import com.example.buynow.presentation.activity.VisualSearchActivity
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var coverRecView: RecyclerView
    private lateinit var newRecView: RecyclerView
    private lateinit var saleRecView: RecyclerView
    private lateinit var coverProduct: ArrayList<Product>
    private lateinit var newProduct: ArrayList<Product>
    private lateinit var saleProduct: ArrayList<Product>

    private lateinit var coverProductAdapter: CoverProductAdapter
    private lateinit var newProductAdapter: ProductAdapter
    private lateinit var saleProductAdapter: SaleProductAdapter

    private lateinit var newLayout: LinearLayout
    private lateinit var saleLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Initialize lists
        coverProduct = arrayListOf()
        newProduct = arrayListOf()
        saleProduct = arrayListOf()

        // Views
        coverRecView = view.findViewById(R.id.coverRecView)
        newRecView = view.findViewById(R.id.newRecView)
        saleRecView = view.findViewById(R.id.saleRecView)
        newLayout = view.findViewById(R.id.newLayout)
        saleLayout = view.findViewById(R.id.saleLayout)

        // RecyclerViews
        coverRecView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        coverProductAdapter = CoverProductAdapter(requireContext(), coverProduct)
        coverRecView.adapter = coverProductAdapter

        newRecView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        newProductAdapter = ProductAdapter(newProduct, requireContext())
        newRecView.adapter = newProductAdapter

        saleRecView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        saleProductAdapter = SaleProductAdapter(saleProduct, requireContext())
        saleRecView.adapter = saleProductAdapter

        fetchHomeProducts()

        // Buttons
        view.findViewById<ImageView>(R.id.visualSearchBtn_homePage).setOnClickListener {
            startActivity(Intent(context, VisualSearchActivity::class.java))
        }

        view.findViewById<ImageView>(R.id.showcaseBtn_homePage).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_fragment, ShowCase())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun fetchHomeProducts() {
        val db = FirebaseFirestore.getInstance()
        var count = 0

        fun checkVisibility() {
            count++
            if (count == 3) {
                coverRecView.visibility = View.VISIBLE
                newLayout.visibility = View.VISIBLE
                saleLayout.visibility = View.VISIBLE
            }
        }

        // COVER Products
        db.collection("Product")
            .whereEqualTo("productCategory", "cover")
            .get()
            .addOnSuccessListener { documents ->
                coverProduct.clear()
                for (doc in documents) {
                    coverProduct.add(doc.toObject(Product::class.java))
                }
                coverProductAdapter.notifyDataSetChanged()
                checkVisibility()
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to fetch cover products", it)
                checkVisibility()
            }

        // NEW Products
        db.collection("Product")
            .whereEqualTo("productCategory", "new")
            .get()
            .addOnSuccessListener { documents ->
                newProduct.clear()
                for (doc in documents) {
                    newProduct.add(doc.toObject(Product::class.java))
                }
                newProductAdapter.notifyDataSetChanged()
                checkVisibility()
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to fetch new products", it)
                checkVisibility()
            }

        // SALE Products (also using "cover" as example, change category if needed)
        db.collection("Product")
            .whereEqualTo("productCategory", "cover")
            .get()
            .addOnSuccessListener { documents ->
                saleProduct.clear()
                for (doc in documents) {
                    saleProduct.add(doc.toObject(Product::class.java))
                }
                saleProductAdapter.notifyDataSetChanged()
                checkVisibility()
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to fetch sale products", it)
                checkVisibility()
            }
    }
}
