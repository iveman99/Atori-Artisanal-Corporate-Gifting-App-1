package com.example.buynow.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.buynow.R
import com.example.buynow.data.local.room.Card.CardEntity

class CardAdapter(
    private val ctx: Context,
    private val listener: CardItemClickAdapter
) : RecyclerView.Adapter<CardAdapter.cardViewHolder>() {

    private val cardList: ArrayList<CardEntity> = arrayListOf()
    private var selectedPosition = -1

    inner class cardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardNumber: TextView = itemView.findViewById(R.id.cardNumber_singleCard)
        val cardHName: TextView = itemView.findViewById(R.id.cardHolderName_singleCard)
        val exp: TextView = itemView.findViewById(R.id.expiryDate_singleCard)
        val layD: LinearLayout = itemView.findViewById(R.id.useDefault_Layout)
        val checkbox: CheckBox = itemView.findViewById(R.id.checkBox_SingleCard)
        val cardImage: ImageView = itemView.findViewById(R.id.cardBrandImage_singleCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cardViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.single_card, parent, false)
        return cardViewHolder(view)
    }

    override fun onBindViewHolder(holder: cardViewHolder, position: Int) {
        val cardItem = cardList[position]
        val brandRes = when (cardItem.brandC) {
            "MASTERCARD" -> R.drawable.ic_mastercard
            "VISA" -> R.drawable.ic_visa
            "AMERICAN_EXPRESS" -> R.drawable.ic_american_express
            "DINERS_CLUB" -> R.drawable.ic_diners_club
            "DISCOVER" -> R.drawable.ic_discover
            "JCB" -> R.drawable.ic_jcb
            "CHINA_UNION_PAY" -> R.drawable.ic_unionpay
            else -> R.drawable.ic_mastercard
        }

        holder.cardImage.setImageResource(brandRes)
        holder.cardHName.text = cardItem.nameCH
        holder.exp.text = cardItem.exp
        holder.cardNumber.text = "**** **** **** ${cardItem.number.takeLast(4)}"
        holder.checkbox.isChecked = position == selectedPosition
        holder.checkbox.setOnClickListener {
            selectedPosition = if (holder.checkbox.isChecked) position else -1
            notifyDataSetChanged()
        }

        holder.layD.visibility = if (cardList.size > 1) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = cardList.size

    fun updateList(newList: List<CardEntity>) {
        cardList.clear()
        cardList.addAll(newList)
        notifyDataSetChanged()
    }

    fun getSelectedCard(): CardEntity? {
        return if (selectedPosition != -1) cardList[selectedPosition] else null
    }
}