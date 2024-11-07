package com.example.store_application

import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.store_application.AppliactionObjs.Buyer
import com.example.store_application.AppliactionObjs.Card
import com.example.store_application.AppliactionObjs.Order
import com.example.store_application.databinding.ActivityBuyerCardBinding
import com.google.firebase.database.FirebaseDatabase
import com.stripe.android.core.utils.ContextUtils.packageInfo

class Buyer_Card_Activity : AppCompatActivity() {
    lateinit var binding :ActivityBuyerCardBinding
     var  buyerEmailReady : Boolean = false
     var  buyerNameReady : Boolean = false
     var  buyerPhoneReady : Boolean = false
   lateinit var  buyerName :String
   lateinit var  buyerEmail :String
   lateinit var  buyerPhone :String
    var  totalPrice :String? = null

     var  cardsList:ArrayList<Card>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
initInputes()
setOrder()
getCardsList()
        goBack()
    }

    private fun goBack() {
        binding.buyerCardGoBack.setOnClickListener {
            finish()
        }
    }

    private fun getCardsList() {
        val cardIntent = intent
        cardsList = cardIntent.getSerializableExtra("cardsList") as? ArrayList<Card>
         totalPrice = intent.getStringExtra("totalPrice")
}

    private fun setOrder() {
        binding.orderBtn.setOnClickListener {
            var firebaseDB = FirebaseDatabase.getInstance()
            var ordersRef = firebaseDB.getReference("Orders")
            var orderId = ordersRef.push().key!!
            var buyer = Buyer(buyerName,buyerName,buyerPhone)
            var order =Order(orderId,cardsList!!,totalPrice!!,buyer)
            ordersRef.child(orderId).setValue(order).addOnCompleteListener {  task ->
                if (task.isSuccessful){
                    Toast.makeText(baseContext , "OrderSuccessful",Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun initInputes() {
        binding.buyerEmailInput.addTextChangedListener { TextWatcher ->
              buyerEmail = binding.buyerEmailInput.text.toString()
            if(buyerEmail.isEmpty()){
                inputEmpty(binding.buyerEmailInput,binding.buyerEmailError,R.string.email_emty)
                buyerEmailReady = false
                inputReady()
            }else if (!buyerEmail.endsWith("@gmail.com")){

                binding.buyerEmailInput.setBackgroundResource(R.drawable.inpute_wrong_backround)
                binding.buyerEmailError.visibility = View.VISIBLE
                binding.buyerEmailError.setText(R.string.email_error)
                buyerEmailReady = false
                inputReady()
            }else{
                buyerEmailReady = true
                inputFull(binding.buyerEmailInput,binding.buyerEmailError)
                inputReady()
            }

        }
        binding.buyerNameInput.addTextChangedListener { TextWatcher ->
              buyerName = binding.buyerNameInput.text.toString()
            if(buyerName.isEmpty()){
                inputEmpty(binding.buyerNameInput,binding.buyerNameError,R.string.name_emty)
                buyerNameReady = false
                inputReady()
            }else{
                buyerNameReady = true
                binding.buyerNameError.visibility = View.GONE
                inputFull(binding.buyerNameInput,binding.buyerNameError)
                inputReady()
            }
        }
        binding.buyerPhoneInput.addTextChangedListener { TextWatcher ->
            buyerPhone = binding.buyerPhoneInput.text.toString()
            if(buyerPhone.isEmpty()){
                inputEmpty(binding.buyerPhoneInput,binding.buyerPhoneError,R.string.phone_emty)
                buyerPhoneReady = false
                inputReady()
            }else if (buyerPhone.length < 9){
                binding.buyerPhoneInput.setBackgroundResource(R.drawable.inpute_wrong_backround)
                binding.buyerPhoneError.visibility = View.VISIBLE
                binding.buyerPhoneError.setText(R.string.phone_error)
                inputReady()
            }else if (buyerPhone.startsWith("07") && buyerPhone.startsWith("06")  || !buyerPhone.startsWith("06") && !buyerPhone.startsWith("07") ){
               if(buyerPhone.startsWith("07") || buyerPhone.startsWith("06") ){
                   buyerPhoneReady = true
                   binding.buyerPhoneError.visibility = View.GONE
                   inputFull(binding.buyerPhoneInput,binding.buyerPhoneError)
                   inputReady()
               }else{
                   binding.buyerPhoneInput.setBackgroundResource(R.drawable.inpute_wrong_backround)
                   binding.buyerPhoneError.visibility = View.VISIBLE
                   binding.buyerPhoneError.setText(R.string.phone_start_num)
                   inputReady()
               }
            }
            else{
                buyerPhoneReady = true
                binding.buyerPhoneError.visibility = View.GONE
                inputFull(binding.buyerPhoneInput,binding.buyerPhoneError)
                inputReady()
            }
        }

    }

    private fun inputFull(input: EditText, errorText: TextView) {
        input.setBackgroundResource(R.drawable.white_input_text_backround_res)
        errorText.visibility = View.GONE
    }
    private fun inputEmpty(input: EditText, errorText: TextView, errorMessage: Int,) {
        input.setBackgroundResource(R.drawable.inpute_wrong_backround)
        errorText.visibility = View.VISIBLE
errorText.setText(errorMessage)


    }
    fun inputReady(){
        if (buyerNameReady && buyerEmailReady && buyerPhoneReady){
            binding.orderBtn.setBackgroundResource(R.drawable.brow_btn_backround)
            binding.orderBtn.isEnabled = true
        }else{
            binding.orderBtn.setBackgroundResource(R.drawable.disable_btns_backroud)
            binding.orderBtn.isEnabled = false
        }
    }

}