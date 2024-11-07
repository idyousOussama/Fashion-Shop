package com.example.store_application

import android.accounts.Account
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.store_application.AppliactionObjs.Accounts
import com.example.store_application.AppliactionObjs.Card
import com.example.store_application.AppliactionObjs.Product
import com.example.store_application.AppliactionObjs.User
import com.example.store_application.databinding.ActivityProductDetailsActivitiesBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class Product_Details_Activities : AppCompatActivity() {
    lateinit var binding: ActivityProductDetailsActivitiesBinding
    val firebaseDB = FirebaseDatabase.getInstance()
 var productDetails : Product? = null
   lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsActivitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getProduct()
        setCart()
        goBack()
    }
    private fun goBack() {
        binding.detailsGoBack.setOnClickListener {
            finish()
        }
    }
  private fun setCart() {
        binding.addTeoCardBtn.setOnClickListener {

            val cartsRef = firebaseDB.getReference("Cards").child(user.userId + "cards")
            var cardId = cartsRef.push().key!!
            var newCard = Card(cardId ,productDetails,1,false)
var exists = false
            cartsRef.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        for (item in p0.children){
                            var card = item.getValue(Card::class.java)
                            if(card!!.cardProduct!!.productId ==  productDetails!!.productId){
                                exists = true
                                Toast.makeText(baseContext , card!!.cardProduct!!.productId , Toast.LENGTH_SHORT).show()
                             var   cardProNum = card.cardsNumber.toInt() + 1
                                var updates =  mapOf<String ,Int>(
                                    "cardsNumber" to  cardProNum
                                )
                                cartsRef.child(card.cardId).updateChildren(updates)
                                Toast.makeText(baseContext,"upDate",Toast.LENGTH_SHORT).show()
                            }
                        }
                         if(!exists){
                             cartsRef.child(cardId).setValue(newCard).addOnCompleteListener { task->
                                 if(task.isSuccessful){
                                     Toast.makeText(baseContext , R.string.card_Added,Toast.LENGTH_SHORT).show()
                                 }else{
                                     Toast.makeText(baseContext , R.string.card_Failure,Toast.LENGTH_SHORT).show()
                                 }
                             }
                         }
                    }else{
                        cartsRef.child(cardId).setValue(newCard)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        }
    }

    private fun getProduct() {
        var productIntent = intent
        productDetails =(productIntent.getSerializableExtra("selectedProduct") as? Product)!!
       user = (productIntent.getSerializableExtra("selectedUser") as? User)!!
        setProduct(productDetails)

    }

    private fun setProduct(product: Product?) {
        Picasso.get().load(product?.productImage).placeholder(R.drawable.large_palce_holder).into(binding.detailsImage)
        binding.detailsProductName.setText(product?.productName)
        binding.productDescription.setText(product?.productDescription)
        binding.totalPrice.setText(product?.productPrice.toString())

    }


    }

