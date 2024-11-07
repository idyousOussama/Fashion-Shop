package com.example.store_application

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.store_application.Adapters.CardAdapter
import com.example.store_application.AppliactionObjs.Card
import com.example.store_application.AppliactionObjs.User
import com.example.store_application.Listeners.CardsListener
import com.example.store_application.databinding.ActivityCardsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Cards_Activity : AppCompatActivity() {
    var firebaseDB = FirebaseDatabase.getInstance()
    var cardsRef =  firebaseDB.getReference("Cards")
    lateinit var binding :ActivityCardsBinding
     var cardAdapter : CardAdapter = CardAdapter()
    var currentUserCard : User? = null
    var cardsPrice = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCardsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getCurrentuserCard()

        goBack()
        getCards()
    setCardNumberChnage()
    setOrder()
       goBackToAddCard()
        setBill()
    }

    private fun setBill() {
        binding.billPrice.setText(cardsPrice.toString())
        binding.delService.setText("5")
        binding.totalBell.setText((cardsPrice +5).toString())
    }

    private fun goBackToAddCard() {
        binding.addCartsBtn.setOnClickListener {
            finish()
        }
    }

    private fun getCurrentuserCard() {
        val curentUserIntent = intent
currentUserCard = curentUserIntent.getSerializableExtra("currentUserCard") as? User
    }

    private fun goBack() {
        binding.arrowLeftBtns.setOnClickListener {
            finish()
        }
    }

    private fun setOrder() {
        binding.continueToOrder.setOnClickListener {
            var intent = Intent(baseContext,Buyer_Card_Activity::class.java)
            intent.putExtra("cardsList",cardAdapter.cardsList)
            intent.putExtra("totalPrice",binding.totalBell.text.toString())
        startActivity(intent)
        }
    }


    private fun alertDialog(card : Card) {
        val dialogView = layoutInflater.inflate(R.layout.delete_card_item_custom_dialog,null)
        val desmissDialogBtn = dialogView.findViewById<ImageButton>(R.id.delete_item_dismess_dialog)
    val deldialoglayout = dialogView.findViewById<RelativeLayout>(R.id.deldialog_layout)
    val progresslayout = dialogView.findViewById<RelativeLayout>(R.id.deleting_Progress_layout)
        val deleteItemsBtn = dialogView.findViewById<Button>(R.id.delete_dialog_deleteBtn)
        var  deleteItemsDialog = Dialog(this)
        deleteItemsDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
 deleteItemsDialog.setContentView(dialogView)
        deleteItemsDialog.setCancelable(false)
        deleteItemsDialog.show()
        desmissDialogBtn.setOnClickListener {
            deleteItemsDialog.dismiss()
        }
        deleteItemsBtn.setOnClickListener {
            deldialoglayout.visibility = View.GONE
            progresslayout.visibility =
                View.VISIBLE
            removeCardsInDB(card,deleteItemsDialog)
        }
    }
    private fun removeCardsInDB(card : Card , dialog : Dialog) {
        val cardRef = cardsRef.child(currentUserCard!!.userId + "cards")

        cardRef.child(card.cardId).removeValue().addOnCompleteListener { task->

            if(task.isSuccessful){
                if(cardAdapter.removeCard(card)){
                    dialog.dismiss()
                    Snackbar.make(binding.root, R.string.deleted_text, Snackbar.LENGTH_SHORT).show()

                }

            }
        }
    }



    private fun setCardNumberChnage() {
        cardAdapter.setListener(object : CardsListener{
            override fun onPlusBtnClick(card: Card, text: TextView) {
                Toast.makeText(baseContext,"Plus" , Toast.LENGTH_SHORT).show()
                card.cardsNumber = card.cardsNumber + 1
                text.setText(card.cardsNumber.toString())
                cardsPrice = cardsPrice+card.cardProduct!!.productPrice
                binding.billPrice.setText(cardsPrice.toString())
                binding.totalBell.setText((cardsPrice + 5).toString())
upDateCardNumber(card.cardsNumber ,card)
            }
            override fun onMinusBtnClick(card: Card, text: TextView) {
           if (card.cardsNumber > 1){
               Toast.makeText(baseContext,"Minus" , Toast.LENGTH_SHORT).show()
               card.cardsNumber = card.cardsNumber - 1
               cardsPrice = cardsPrice+card.cardProduct!!.productPrice
               binding.billPrice.setText(cardsPrice.toString())
               binding.totalBell.setText((cardsPrice + 5).toString())
               text.setText(card.cardsNumber.toString())
               upDateCardNumber(card.cardsNumber ,card)
           }else{
               alertDialog(card)
           }
            }

            override fun onItemLongClicked(itemView: RelativeLayout , card : Card) {
            alertDialog(card)
            }


        })
    }



    private fun upDateCardNumber(cardsNumber: Int, card: Card) {
var cardRef = firebaseDB.getReference("Cards").child(currentUserCard!!.userId + "cards").child(card.cardId)

        val updates = mapOf<String, Any>(
            "cardsNumber" to cardsNumber
        )

        cardRef.updateChildren(updates)
    }

    private fun checkcards(isExist: Boolean) {
if(isExist){
    binding.cardsProgress.visibility = View.GONE
    binding.cartsList.visibility = View.VISIBLE
    binding.bellLayout.visibility = View.VISIBLE
}else{
    binding.cardsProgress.visibility = View.GONE
    binding.noCardLayout.visibility = View.VISIBLE
}

    }

    private fun getCards() {

    cardsRef.child(currentUserCard!!.userId + "cards").addListenerForSingleValueEvent(object : ValueEventListener{
var cardsList :  ArrayList<Card> = ArrayList()
        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()){
                for (items in p0.children){
                    var card = items.getValue(Card::class.java)
                    if (card != null) {
                        cardsList.add(card)
                        cardsPrice =cardsPrice + card.cardProduct!!.productPrice
                    }
                }
                if(cardsList.size > 0){
                    checkcards(true)
                    setCardsList(cardsList)
                }else{
                    checkcards(false)

                }

            }else{
                checkcards(false)
            }
        }

        override fun onCancelled(p0: DatabaseError) {
            TODO("Not yet implemented")
        }
    })
    }

    private fun setCardsList(cardsList: ArrayList<Card>) {
        cardAdapter.setList(cardsList)
        binding.cartsList.layoutManager = LinearLayoutManager(this)
        binding.cartsList.setHasFixedSize(true)
        binding.cartsList.adapter = cardAdapter

    }

    }