package com.example.store_application.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.store_application.AppliactionObjs.Card
import com.example.store_application.Listeners.CardsListener
import com.example.store_application.R
import com.squareup.picasso.Picasso
class CardAdapter(): RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    lateinit var lestener: CardsListener
    var cardsList: ArrayList<Card> = ArrayList()

    fun setList(cardList: ArrayList<Card>) {
        this.cardsList = cardList
        notifyDataSetChanged()
    }

    fun setListener(listener: CardsListener) {
        this.lestener= listener
    }





    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CardViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.card_custom_product, p0, false)
        return CardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cardsList.size
    }

    override fun onBindViewHolder(p0: CardViewHolder, p1: Int) {
        val card = cardsList[p1]
        card.cardPsition = p1
        p0.setCard(
            card.cardProduct?.productImage ?: "",
            card.cardProduct?.productPrice ?: 0,
            card.cardProduct?.productName ?: "",
            card.cardsNumber ?: 0
        )

        p0.plusBtn.setOnClickListener {
            lestener.onPlusBtnClick(card, p0.cardNumber)
        }

        p0.minusBtn.setOnClickListener {
            lestener.onMinusBtnClick(card, p0.cardNumber)
        }

        p0.cardView.setOnLongClickListener {
            handleLongClick(card, p0.cardView)
            true
        }


    }

    private fun handleLongClick(card: Card, cardView: RelativeLayout) {

        lestener.onItemLongClicked(cardView, card)
    }

    fun removeCard(card: Card) : Boolean {
        cardsList.remove(card)
        notifyItemRemoved(card.cardPsition)
        return true

    }
    class CardViewHolder(itemView: View) : ViewHolder(itemView) {
        var cardView: RelativeLayout = itemView.findViewById(R.id.card_card)
        private var cardImage: ImageView = itemView.findViewById(R.id.card_custom_img)
        private var cardPrice: TextView = itemView.findViewById(R.id.card_costom_price)
        private var cardProName: TextView = itemView.findViewById(R.id.card_costom_name)
        var cardNumber: TextView = itemView.findViewById(R.id.cards_num_bar)
        var plusBtn: ImageView = itemView.findViewById(R.id.plus_card_bnt)
        var minusBtn: ImageView = itemView.findViewById(R.id.minus_card_bnt)

        fun setCard(cardImg: String, cardPr: Int, cardNam: String, cardNum: Int) {
            Picasso.get().load(cardImg.toUri()).placeholder(R.drawable.place_holder_image).into(cardImage)
            cardPrice.text = cardPr.toString() + " "
            cardProName.text = cardNam
            cardNumber.text = cardNum.toString()
        }
    }
}
