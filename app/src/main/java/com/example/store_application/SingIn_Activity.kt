package com.example.store_application

import android.accounts.Account
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.store_application.AppliactionObjs.Accounts

import com.example.store_application.AppliactionObjs.User
import com.example.store_application.RoomDatabase.StoreViewModele
import com.example.store_application.databinding.ActivitySingInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingIn_Activity : AppCompatActivity() {
    private lateinit var binding :ActivitySingInBinding
private var emailIsSuccessful= false
private var passWordIsSuccessful= false
var firebaseDB = FirebaseDatabase.getInstance()
    var account : Account? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sginInClicked()
        setForgetEmail()
        signIn()
        showAndHidepassword()
        getLogainedAccount()
    }

    private fun getLogainedAccount() {
        var logainedAccountIntent = intent
        var email = logainedAccountIntent.getStringExtra("logainedAccount")
        if (email!= null){
            binding.signInUseremail.setText(email)
        }
    }

    private fun showAndHidepassword() {
        var isPasswordVisible = false

        binding.sginInShowPassword.setOnClickListener {
            if (isPasswordVisible) {
                // Hide password
                binding.signInUserPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.sginInShowPassword.setImageResource(R.drawable.close_eyes)
                isPasswordVisible = false
            } else {
                // Show password
                binding.signInUserPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.sginInShowPassword.setImageResource(R.drawable.open_eyes)
                isPasswordVisible = true
            }
            // Move cursor to the end of the text
            binding.signInUserPassword.setSelection(binding.signInUserPassword.text.length)
        }
    }
    private fun setForgetEmail() {
        binding.forgetPassword.setOnClickListener{
            if(emailIsSuccessful){
                var intent = Intent(baseContext,Forget_Password_Activity::class.java)
                intent.putExtra("forgetUserEmail" , binding.signInUseremail.text.toString())
                startActivity(intent)
            }else{
                Toast.makeText(baseContext,"Please Add Email",Toast.LENGTH_SHORT).show()
            }

        }
    }

 private fun signIn() {
binding.signInBtn.setOnClickListener {
if(emailIsSuccessful&&passWordIsSuccessful){
    initSginInBtn(1)
    checkAccountIsExists(binding.signInUseremail.text.toString(),binding.signInUserPassword.text.toString())
}

}
    }

    private fun initSginInBtn(i: Int) {
        if (i == 1){
            binding.signInBtn.visibility = View.GONE
            binding.sginInProgress.visibility = View.VISIBLE
        }else{
            binding.signInBtn.visibility = View.VISIBLE
            binding.sginInProgress.visibility = View.GONE
        }

    }

    private fun checkAccountIsExists(accountEmail : String ,accountPassword: String) {
        var firebaseAuth = FirebaseAuth.getInstance()
         firebaseAuth.signInWithEmailAndPassword(accountEmail , accountPassword).addOnCompleteListener {task ->
             if(task.isSuccessful){
                 val user  = firebaseAuth.currentUser
                 val userUid = user?.uid.toString()

                 accountFetshByUID(userUid)
             }else{
                 initSginInBtn(2)
                 var exception =task.exception
                 when(exception){
         is FirebaseAuthInvalidCredentialsException  ->{
             if(exception.toString() == "ERROR_INVALID_EMAIL"){
                 Toast.makeText(baseContext,"Email wrong please channge Email and Try Again",Toast.LENGTH_SHORT).show()
             }else if(exception.toString() == "ERROR_INVALID_PASSWORD"){
                 Toast.makeText(baseContext,"password wrong change your password and tryAgain",Toast.LENGTH_SHORT).show()
             }
         }
         is FirebaseAuthInvalidUserException ->{
             if (exception.toString() == "ERROR_USER_DISABLED") {
                 Toast.makeText(baseContext,"This Account has been Disable",Toast.LENGTH_SHORT).show()
             }else if (exception.toString()=="ERROR_USER_NOT_FOUND"){
                 Toast.makeText(baseContext,"Account No found plase change your info and try again",Toast.LENGTH_SHORT).show()
             }
         }
     } }
         }.addOnFailureListener {
             initSginInBtn(2)
         }
    }
    private fun accountFetshByUID(userUid: String) {
var userRef = firebaseDB.getReference("Users").child(userUid)
        userRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    initSginInBtn(2)
                    val user = p0.getValue(User::class.java)
                    insertToRoom(user!!)
                }else{
                    initSginInBtn(2)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                initSginInBtn(2)
            }

        })
    }

    private fun insertToRoom(user :User) {
       val roomViewModel = ViewModelProvider(this).get(StoreViewModele::class)
        CoroutineScope(Dispatchers.Main).launch{
        var userAccount = roomViewModel.getAccountByEmail(user.userAccount!!.email)
        if (userAccount != null){
            CoroutineScope(Dispatchers.Main).launch{
val currentLogainedAccount = roomViewModel.getLogainedAccount()
                if (currentLogainedAccount != null){
                    CoroutineScope(Dispatchers.Main).launch{
roomViewModel.updateAccountLogainStatus(currentLogainedAccount.email,false)
                        CoroutineScope(Dispatchers.Main).launch{
                            val currentAccountLogain = Accounts(user.userAccount!!.email,user.userAccount!!.accountuserId,user.userAccount!!.accountProfile,user.userAccount!!.accountPassword,user.userAccount!!.accountName,true)
                            roomViewModel.insertNewAccount(currentAccountLogain)
                            goToIntent(user)
                        }
                    }
                }else{
                    CoroutineScope(Dispatchers.Main).launch{
                        val currentAccountLogain = Accounts(user.userAccount!!.email,user.userAccount!!.accountuserId,user.userAccount!!.accountProfile,user.userAccount!!.accountPassword,user.userAccount!!.accountName,true)
                   roomViewModel.insertNewAccount(currentAccountLogain)
                        goToIntent(user)
                    }
                }
            }
        }else{
            CoroutineScope(Dispatchers.Main).launch{
                val currentLogainedAccount = roomViewModel.getLogainedAccount()
                if (currentLogainedAccount != null){
                    CoroutineScope(Dispatchers.Main).launch{
                        roomViewModel.updateAccountLogainStatus(currentLogainedAccount.email,false)
                        CoroutineScope(Dispatchers.Main).launch{
                            val currentAccountLogain = Accounts(user.userAccount!!.email,user.userAccount!!.accountuserId,user.userAccount!!.accountProfile,user.userAccount!!.accountPassword,user.userAccount!!.accountName,true)
                            roomViewModel.insertNewAccount(currentAccountLogain)
                            goToIntent(user)
                        }
                    }
                }else{
                    CoroutineScope(Dispatchers.Main).launch{
                        val currentAccountLogain = Accounts(user.userAccount!!.email,user.userAccount!!.accountuserId,user.userAccount!!.accountProfile,user.userAccount!!.accountPassword,user.userAccount!!.accountName,true)
                        roomViewModel.insertNewAccount(currentAccountLogain)
                        goToIntent(user)
                    }
                }
            }
        }
     }

    }

    private fun goToIntent(user: User) {
        if(user != null){
            var userIntent = Intent(baseContext,Splach_Activity::class.java)
            startActivity(userIntent)
        }
    }
    private fun sginInClicked() {
        binding.signInUseremail.addTextChangedListener { TextWatcher ->
          var emailText : String = binding.signInUseremail.text.toString().trim()

            if (emailText.isEmpty() || !emailText.endsWith("@gmail.com")){
                binding.signInUseremail.setBackgroundResource(R.drawable.inpute_wrong_backround)
                emailIsSuccessful = false
                enableSginInBtn()
            }else{
                binding.signInUseremail.setBackgroundResource(R.drawable.white_input_text_backround_res)
                emailIsSuccessful =true
                enableSginInBtn()
            }
        }
        binding.signInUserPassword.addTextChangedListener { TextWatcher ->
          var passwordText : String = binding.signInUseremail.text.toString().trim()

            if (passwordText.isEmpty()){
                binding.signInUserPassword.setBackgroundResource(R.drawable.inpute_wrong_backround)
                passWordIsSuccessful = false
                enableSginInBtn()
            }else{
                binding.signInUserPassword.setBackgroundResource(R.drawable.white_input_text_backround_res)
                passWordIsSuccessful =true
                enableSginInBtn()
            }
        }
        }

    private fun enableSginInBtn() {
        if(passWordIsSuccessful && emailIsSuccessful){
            binding.signInBtn.isEnabled = true
            binding.singBtnLayout.setBackgroundResource(R.drawable.brow_btn_backround)
        }else{
            binding.signInBtn.isEnabled = false
            binding.singBtnLayout.setBackgroundResource(R.drawable.disable_btns_backroud)
        }
    }


}