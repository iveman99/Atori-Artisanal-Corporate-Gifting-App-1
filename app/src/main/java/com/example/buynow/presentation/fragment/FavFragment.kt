package com.example.buynow.presentation.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.buynow.R
import com.example.buynow.data.model.Product
import com.example.buynow.presentation.adapter.ProductAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavFragment : Fragment() {

    private lateinit var animationView: LottieAnimationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyBagLayout: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var favAdapter: ProductAdapter
    private var favList: ArrayList<Product> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_fav, container, false)
        animationView = view.findViewById(R.id.animationViewFavPage)
        recyclerView = view.findViewById(R.id.recyclerViewFav)
        emptyBagLayout = view.findViewById(R.id.emptyBagMsgLayout)

        sharedPreferences = requireActivity().getSharedPreferences("Favorites", Context.MODE_PRIVATE)

        loadFavorites()

        return view
    }

    private fun loadFavorites() {
        val gson = Gson()
        val favoritesJson = sharedPreferences.getString("favList", "[]")
        val type = object : TypeToken<ArrayList<Product>>() {}.type
        favList = gson.fromJson(favoritesJson, type)

        if (favList.isEmpty()) {
            showEmptyAnimation()
        } else {
            showFavoritesList()
        }
    }

    private fun showEmptyAnimation() {
        emptyBagLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        animationView.playAnimation()
        animationView.loop(true)
    }

    private fun showFavoritesList() {
        emptyBagLayout.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        favAdapter = ProductAdapter(favList, requireContext())

        favAdapter.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onFavoriteClick(product: Product, position: Int) {
                removeFromFavorites(product, position)
            }
        })

        recyclerView.adapter = favAdapter
    }

    private fun removeFromFavorites(product: Product, position: Int) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val favoritesJson = sharedPreferences.getString("favList", "[]")
        val type = object : TypeToken<ArrayList<Product>>() {}.type
        val favoritesList: ArrayList<Product> = gson.fromJson(favoritesJson, type)

        // Remove the product from the favorites list
        favoritesList.removeAll { it.productName == product.productName }

        // Save updated list back to SharedPreferences
        editor.putString("favList", gson.toJson(favoritesList))
        editor.apply()

        // Remove from displayed list and update RecyclerView
        favList.removeAt(position)
        favAdapter.notifyItemRemoved(position)
        favAdapter.notifyItemRangeChanged(position, favList.size)

        // If no favorites left, show the empty state animation
        if (favList.isEmpty()) {
            showEmptyAnimation()
        }

        Toast.makeText(requireContext(), "${product.productName} removed from Favorites", Toast.LENGTH_SHORT).show()
    }
}
