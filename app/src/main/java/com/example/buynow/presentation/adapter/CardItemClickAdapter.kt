package com.example.buynow.presentation.adapter

import com.example.buynow.data.local.room.Card.CardEntity

interface CardItemClickAdapter {
    fun onItemDeleteClick(cardEntity: CardEntity)
    fun onItemUpdateClick(cardEntity: CardEntity)
}
