package com.example.buynow.presentation.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buynow.EcoFriendly
import com.example.buynow.Humidifiers
import com.example.buynow.Mugs
import com.example.buynow.presentation.adapter.CategoryAdapter
import com.example.buynow.presentation.adapter.CoverProductAdapter
import com.example.buynow.data.model.Category
import com.example.buynow.data.model.Product
import com.example.buynow.R
import com.example.buynow.TableLamp
import com.example.buynow.digital
import com.example.buynow.other
import com.example.buynow.torch
import com.example.buynow.hamper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class ShopFragment : Fragment() {

    private lateinit var cateList: ArrayList<Category>
    private lateinit var coverProduct: ArrayList<Product>

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var coverProductAdapter: CoverProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_shop, container, false)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val coverRecViewShopFrag: RecyclerView = view.findViewById(R.id.coverRecView_shopFrag)
        val categoriesRecView: RecyclerView = view.findViewById(R.id.categoriesRecView)

        cateList = arrayListOf()
        coverProduct = arrayListOf()

        setCoverData()
        setCategoryData()

        coverRecViewShopFrag.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        coverRecViewShopFrag.setHasFixedSize(true)
        coverProductAdapter = CoverProductAdapter(requireContext(), coverProduct)
        coverRecViewShopFrag.adapter = coverProductAdapter

        categoriesRecView.layoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        categoriesRecView.setHasFixedSize(true)

        // 🔥 Handle Category Click
        val categoryMap = mapOf(
            "Digital Clocks" to digital::class.java,
            "Mugs And Flasks" to Mugs::class.java,
            "Humidifiers" to Humidifiers::class.java,
            "Torches" to torch::class.java,
            "Table Lamp" to TableLamp::class.java,
            "Ecofriendly" to EcoFriendly::class.java,
            "Festival Hamper" to hamper::class.java,
            "Other" to other::class.java

        )

        categoryAdapter = CategoryAdapter(requireContext(), cateList) { category ->
            categoryMap[category.Name]?.let { activityClass ->
                val intent = Intent(requireContext(), activityClass)
                startActivity(intent)
            }
        }
        categoriesRecView.adapter = categoryAdapter

        return view
    }

    private fun setCategoryData() {
        cateList.add(Category("Digital Clocks", "https://images-cdn.ubuy.co.in/63b585531468c93ebd489b23-txl-large-digital-wall-clock-with.jpg"))
        cateList.add(Category("Mugs And Flasks", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRF11eEpVkIqGpMPf_QulQgxD9lBOE45IIwRA&s"))
        cateList.add(Category("Humidifiers", "https://m.media-amazon.com/images/I/51bANxBeYUL.jpg"))
        cateList.add(Category("Torches", "https://rukminim2.flixcart.com/image/850/1000/l0lbrm80/telescope/s/l/k/60-3-x-led-magnetic-torch-telescope-flexible-flashlight-original-imagccmm5kxqepbh.jpeg?q=20&crop=false"))
        cateList.add(Category("Table Lamp", "https://images.unsplash.com/photo-1517991104123-1d56a6e81ed9?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8VGFibGUlMjBMYW1wc3xlbnwwfHwwfHx8MA%3D%3D"))
        cateList.add(Category("Ecofriendly", "https://media.istockphoto.com/id/2153941669/photo/notebook-and-leaf-on-stone-texture-background-work-desk-space.webp?a=1&b=1&s=612x612&w=0&k=20&c=pdCy-RuPu9YDcM_SzVrBLFtYtvTjSLbP88_AE0xauAA="))
        cateList.add(Category("Festival Hamper", "https://static.wixstatic.com/media/8c63ce_fe07a2bc868f4c9dbff369dce06b3c19~mv2.jpg/v1/fill/w_336,h_449,al_c,q_80,usm_0.66_1.00_0.01,enc_avif,quality_auto/8c63ce_fe07a2bc868f4c9dbff369dce06b3c19~mv2.jpg"))
        cateList.add(Category("Other", "https://media.istockphoto.com/id/1252716015/photo/electric-white-kettle-pouring-water-in-cup-on-table-on-grey-background.webp?a=1&b=1&s=612x612&w=0&k=20&c=EIE7GcCFiIOqhPee8Rg2-X2UpG7f8qOCcVlMZlQi60M="))
    }

    private fun getJsonData(context: Context, fileName: String): String? {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            Log.e("ShopFragment", "Error loading JSON", ioException)
            null
        }
    }

    private fun setCoverData() {
        val jsonFileString = context?.let { getJsonData(it, "CoverProducts.json") }
        if (jsonFileString.isNullOrEmpty()) {
            // Handle the error (e.g., show a message or load a default list)
            Log.e("ShopFragment", "Cover products data is empty or could not be loaded.")
            return
        }

        val gson = Gson()
        val listCoverType = object : TypeToken<List<Product>>() {}.type
        val coverD: List<Product> = gson.fromJson(jsonFileString, listCoverType)
        coverD.forEach { coverProduct.add(it) }
    }
}
