package com.example.buynow.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buynow.R
import com.example.buynow.data.local.room.Card.CardEntity
import com.example.buynow.data.local.room.Card.CardViewModel
import com.example.buynow.databinding.CardAddBottomSheetBinding
import com.example.buynow.presentation.adapter.CardAdapter
import com.example.buynow.presentation.adapter.CardItemClickAdapter
import com.example.buynow.utils.CardType
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PaymentMethodActivity : AppCompatActivity(), CardItemClickAdapter {

    private lateinit var cardViewModel: CardViewModel
    private lateinit var cardAdapter: CardAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView: View
    private var selectedPaymentMethod: String = "Cash"

    private var shippingAddress: String = ""
    private var userName: String = ""
    private var totalAmount: Int = 0
    private var productNames: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        shippingAddress = intent.getStringExtra("SHIPPING_ADDRESS") ?: ""
        userName = intent.getStringExtra("USER_NAME") ?: "User"
        totalAmount = intent.getIntExtra("TOTAL_AMOUNT", 0)
        productNames = intent.getStringExtra("PRODUCT_NAMES") ?: "Unknown Product"

        cardViewModel = ViewModelProvider(this).get(CardViewModel::class.java)

        val cardRecycler = findViewById<RecyclerView>(R.id.cardRecView_paymentMethodPage)
        val addCardFab = findViewById<FloatingActionButton>(R.id.addCard_PaymentMethodPage)
        val placeOrderBtn = findViewById<Button>(R.id.continueToOrderBtn)
        val radioGroup = findViewById<RadioGroup>(R.id.paymentRadioGroup)

        cardAdapter = CardAdapter(this, this)
        cardRecycler.layoutManager = LinearLayoutManager(this)
        cardRecycler.adapter = cardAdapter

        cardViewModel.allCards.observe(this, Observer {
            cardAdapter.updateList(it)
        })

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedPaymentMethod = if (checkedId == R.id.radioCash) "Cash" else "Card"
        }

        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val cardAddBinding = CardAddBottomSheetBinding.inflate(LayoutInflater.from(this))
        bottomSheetView = cardAddBinding.root

        addCardFab.setOnClickListener {
            showBottomSheet()
        }

        placeOrderBtn.setOnClickListener {
            if (selectedPaymentMethod == "Card") {
                val selectedCard = cardAdapter.getSelectedCard()
                if (selectedCard != null) {
                    goToOrderConfirmation(selectedCard.nameCH, selectedCard.brandC)
                } else {
                    Toast.makeText(this, "Please select a card.", Toast.LENGTH_SHORT).show()
                }
            } else {
                goToOrderConfirmation("Cash User", "N/A")
            }
        }
    }

    private fun goToOrderConfirmation(cardHolder: String, cardBrand: String) {
        val intent = Intent(this, OrderConfirmationActivity::class.java)
        intent.putExtra("CARD_HOLDER", cardHolder)
        intent.putExtra("CARD_BRAND", cardBrand)
        intent.putExtra("PAYMENT_METHOD", selectedPaymentMethod)
        intent.putExtra("SHIPPING_ADDRESS", shippingAddress)
        intent.putExtra("USER_NAME", userName)
        intent.putExtra("TOTAL_AMOUNT", totalAmount)
        intent.putExtra("PRODUCT_NAMES", productNames)
        startActivity(intent)
    }

    private fun showBottomSheet() {
        bottomSheetView.findViewById<EditText>(R.id.nameEt_cardAddBottomSheet).text.clear()
        bottomSheetView.findViewById<EditText>(R.id.cardNumber_cardAddBottomSheet).text.clear()
        bottomSheetView.findViewById<EditText>(R.id.exp_cardAddBottomSheet).text.clear()
        bottomSheetView.findViewById<EditText>(R.id.cvv_cardAddBottomSheet).text.clear()

        bottomSheetView.findViewById<Button>(R.id.addCardBtn_cardAddBottomSheet).setOnClickListener {
            saveCardData()
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun saveCardData() {
        val name = bottomSheetView.findViewById<EditText>(R.id.nameEt_cardAddBottomSheet).text.toString()
        val number = bottomSheetView.findViewById<EditText>(R.id.cardNumber_cardAddBottomSheet).text.toString()
        val exp = bottomSheetView.findViewById<EditText>(R.id.exp_cardAddBottomSheet).text.toString()
        val cvv = bottomSheetView.findViewById<EditText>(R.id.cvv_cardAddBottomSheet).text.toString()

        if (name.isEmpty() || number.isEmpty() || exp.isEmpty() || cvv.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val brand = CardType.detect(number).toString()
            cardViewModel.insert(CardEntity(name, number, exp, cvv, brand))
            Toast.makeText(this, "Card Added Successfully", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.dismiss()
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid Card", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemDeleteClick(cardEntity: CardEntity) {
        cardViewModel.deleteCart(cardEntity)
        Toast.makeText(this, "Card Deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onItemUpdateClick(cardEntity: CardEntity) {
        cardViewModel.updateCart(cardEntity)
    }
}
