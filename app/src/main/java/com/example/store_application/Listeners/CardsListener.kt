package com.example.store_application.Listeners

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.store_application.AppliactionObjs.Card

interface CardsListener {
    fun onPlusBtnClick (card : Card, text : TextView)
    fun onMinusBtnClick (card : Card,text : TextView)
    fun onItemLongClicked(itemView : RelativeLayout, card :Card)

}