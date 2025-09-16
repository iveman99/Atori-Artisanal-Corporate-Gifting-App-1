package com.example.buynow.presentation.fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.buynow.R
import com.example.buynow.ShippingAddressActiivty
import com.example.buynow.data.local.room.CartViewModel
import com.example.buynow.data.local.room.ProductEntity
import com.example.buynow.presentation.adapter.CartAdapter

class BagFragment : Fragment(), CartAdapter.CartClickListener {

    private lateinit var cartRecView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var animationView: LottieAnimationView
    private lateinit var totalPriceBagFrag: TextView
    private lateinit var checkOutBtn: Button
    private lateinit var bottomCartLayout: LinearLayout
    private lateinit var emptyBagMsgLayout: LinearLayout
    private lateinit var myBagText: TextView

    private lateinit var cartViewModel: CartViewModel
    private var cartItems: MutableList<ProductEntity> = mutableListOf()
    private var sum: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bag, container, false)

        // Initialize UI
        cartRecView = view.findViewById(R.id.cartRecView)
        animationView = view.findViewById(R.id.animationViewCartPage)
        totalPriceBagFrag = view.findViewById(R.id.totalPriceBagFrag)
        checkOutBtn = view.findViewById(R.id.checkOut_BagPage)
        bottomCartLayout = view.findViewById(R.id.bottomCartLayout)
        emptyBagMsgLayout = view.findViewById(R.id.emptyBagMsgLayout)
        myBagText = view.findViewById(R.id.MybagText)

        animationView.playAnimation()
        animationView.loop(true)

        // Setup RecyclerView
        cartAdapter = CartAdapter(requireContext(), this)
        cartRecView.layoutManager = LinearLayoutManager(requireContext())
        cartRecView.adapter = cartAdapter

        // ViewModel
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        cartViewModel.allproducts.observe(viewLifecycleOwner) { list ->
            cartItems.clear()
            cartItems.addAll(list)
            cartAdapter.updateList(cartItems)
            updateTotalPrice(cartItems)

            // Toggle UI
            if (cartItems.isEmpty()) {
                animationView.visibility = View.VISIBLE
                bottomCartLayout.visibility = View.GONE
                myBagText.visibility = View.GONE
                emptyBagMsgLayout.visibility = View.VISIBLE
            } else {
                animationView.visibility = View.GONE
                bottomCartLayout.visibility = View.VISIBLE
                myBagText.visibility = View.VISIBLE
                emptyBagMsgLayout.visibility = View.GONE
            }
        }

        // Checkout
        checkOutBtn.setOnClickListener {
            if (cartItems.isEmpty()) {
                Toast.makeText(context, "Bag is empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(requireContext(), ShippingAddressActiivty::class.java)
            intent.putExtra("TOTAL_AMOUNT", sum)
            val productNames = cartItems.joinToString(", ") { it.name }
            intent.putExtra("PRODUCT_NAMES", productNames)
            startActivity(intent)
        }

        return view
    }

    override fun onItemDeleteClick(product: ProductEntity) {
        cartViewModel.deleteCart(product)
        Toast.makeText(context, "Removed from bag", Toast.LENGTH_SHORT).show()
    }

    override fun onItemUpdateClick(product: ProductEntity) {
        cartViewModel.updateCart(product)

        // ✅ Update quantity manually in local list so UI & total refresh correctly
        val index = cartItems.indexOfFirst { it.id == product.id }
        if (index != -1) {
            cartItems[index] = product.copy()
            cartAdapter.updateList(cartItems)  // 🔁 Refresh RecyclerView
        }

        updateTotalPrice(cartItems)
    }


    private fun updateTotalPrice(list: List<ProductEntity>) {
        sum = list.sumOf {
            val quantity = it.qua
            val price = it.price.toIntOrNull() ?: 0
            android.util.Log.d(
                "PRICE-CHECK",
                "name=${it.name}, price=${it.price}, quantity=${it.qua}, total=${price * quantity}"
            )
            quantity * price
        }
        totalPriceBagFrag.text = "₹$sum"
    }
}
