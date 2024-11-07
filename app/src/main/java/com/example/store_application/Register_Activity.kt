package com.example.store_application

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.store_application.AppliactionObjs.Accounts
import com.example.store_application.AppliactionObjs.User
import com.example.store_application.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class Register_Activity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    var userNameIsRight = false
    var userEmailIsRight = false
    var userPasswordIsRight = false
    var userConPasswordIsRight = false
    lateinit  var accountImageUri : Uri
    lateinit var userProfileLancher : ActivityResultLauncher<Intent>
    lateinit var userSex:String
    var usersAuth = FirebaseAuth.getInstance()
    var dialog:Dialog? = null
    var userPasswordText : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkInputes()
        registerBtnClicked()
        getUserProfile()
        userProfileLancherfun()
        selectUserSex()
        showAndHidePassword()
    }
    private fun showAndHidePassword() {

        var isPasswordVisible = false

        binding.showPassword.setOnClickListener {
            if (isPasswordVisible) {
                // Hide password
                binding.userPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.showPassword.setImageResource(R.drawable.close_eyes)
                isPasswordVisible = false
            } else {
                // Show password
                binding.userPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.showPassword.setImageResource(R.drawable.open_eyes)
                isPasswordVisible = true
            }
            // Move cursor to the end of the text
            binding.userPassword.setSelection(binding.userPassword.text.length)
        }

}
    private fun userProfileLancherfun() {
        userProfileLancher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val selectedImageUri: Uri? = result.data?.data
                binding.userRegisterPro.setImageURI(selectedImageUri)
                if (selectedImageUri != null) {
                    accountImageUri = selectedImageUri
                }
            }
        }
    }
    private fun createNewuser(userEmail:String, userPassword:String) {
                usersAuth.createUserWithEmailAndPassword(userEmail ,userPassword).addOnCompleteListener { task->
            if(task.isSuccessful){
                var userId = usersAuth.currentUser?.uid
                if (userId != null) {
                    Toast.makeText(baseContext,userId,Toast.LENGTH_SHORT).show()
alertDialog(userId)
                }

            }else{
                if(task.exception is FirebaseAuthUserCollisionException){
                   emailUsedError()

                }
            }

        }
    }
    private fun emailUsedError() {
        binding.userEmailError.setText(R.string.email_alredy_used)
        binding.userEmailError.visibility = View.VISIBLE
        binding.registerProgress.visibility = View.GONE
        binding.signInText.visibility = View.VISIBLE
        userEmailIsRight = false
        enableRgisterBtn()
    }
    private fun insertNewUser(userId : String ,uriImage: String){
var firebaseDB = FirebaseDatabase.getInstance()
var accountsRef  =   firebaseDB.getReference("Users")
var userName = binding.username.text.toString()
var accountEmail = binding.useremail.text.toString()
var accountPassword= binding.userPassword.text.toString()
        var account = Accounts(accountEmail,userId,uriImage, accountPassword , userName ,false)
        var user = User(userId , userSex,account)
        accountsRef.child(userId).setValue(user).addOnCompleteListener {task ->
            if(task.isSuccessful){
                initProgressRegisterBtn(2)
                sucessefulDilaod()
                val  intent = Intent(baseContext,SingIn_Activity::class.java)
                startActivity(intent )
            }else{
            }
        }
    }
    private fun getUserProfile() {
        binding.userRegisterPro.setOnClickListener {
if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
    ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
}else{
var userProfileintent =  Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    userProfileLancher?.launch(userProfileintent)
}}
    }
    private fun registerBtnClicked() {
        binding.registerBtn.setOnClickListener {
            initProgressRegisterBtn(1)
createNewuser(binding.useremail.text.toString(),binding.userPassword.text.toString())
        }
    }
    private fun initProgressRegisterBtn(i: Int) {
        if (i == 1){
            binding.registerBtn.visibility = View.GONE
            binding.registerProgress.visibility = View.VISIBLE
        }else{
            binding.registerBtn.visibility = View.VISIBLE
            binding.registerProgress.visibility = View.GONE
        }

    }
    private fun enableRgisterBtn() {
        if (userNameIsRight && userEmailIsRight && userPasswordIsRight && userConPasswordIsRight ){
            binding.registerBtn.isEnabled = true
            binding.registerBtnLayout.setBackgroundResource(R.drawable.brow_btn_backround)

        }else{

            binding.registerBtn.isEnabled = false
            binding.registerBtnLayout.setBackgroundResource(R.drawable.disable_btns_backroud)
        }
    }
    private fun checkInputes() {
        binding.username.addTextChangedListener { TextWatcher ->

            var userNameText: String = binding.username.text.toString().trim()
            if (userNameText.isEmpty() || userNameText.length < 6) {
                if (userNameText.isEmpty()) {
                    binding.username.setBackgroundResource(R.drawable.inpute_wrong_backround)
                    binding.userNameError.visibility = View.GONE
                    binding.username.setHint(R.string.user_empty_error)
                    userNameIsRight = false
                    enableRgisterBtn()
                }
                else if (userNameText.length < 6) {
                    binding.userNameError.visibility = View.VISIBLE
                    binding.userNameError.setText(R.string.less_char_name_error)
                    binding.username.setBackgroundResource(R.drawable.inpute_wrong_backround)
                    userNameIsRight = false
                    enableRgisterBtn()
                }
            }else {
                binding.username.setBackgroundResource(R.drawable.white_input_text_backround_res)
                binding.userNameError.visibility = View.GONE
                userNameIsRight = true
                enableRgisterBtn()

            }

        }
        binding.useremail.addTextChangedListener { TextWatcher ->
            var userEmailText = binding.useremail.text.toString()
            if (userEmailText.isEmpty() || !userEmailText.endsWith("@gmail.com")) {
                if (userEmailText.isEmpty()) {
                    binding.useremail.setBackgroundResource(R.drawable.inpute_wrong_backround)

                    binding.userEmailError.setHint(R.string.email_empty_error_txt)
                    binding.userEmailError.visibility = View.GONE

                    userEmailIsRight = false
                    enableRgisterBtn()


                }else if( !userEmailText.endsWith("@gmail.com")){
                    binding.useremail.setBackgroundResource(R.drawable.inpute_wrong_backround)
                    binding.userEmailError.setText(R.string.email_error)
                    binding.userEmailError.visibility = View.VISIBLE
                    userEmailIsRight = false
                    enableRgisterBtn()


                }
            } else {
                binding.useremail.setBackgroundResource(R.drawable.white_input_text_backround_res)
                binding.userEmailError.visibility = View.GONE
                userEmailIsRight = true
                enableRgisterBtn()

            }
        }
        binding.userPassword.addTextChangedListener { TextWatcher ->
            val passwordRegex =     Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$")
             userPasswordText = binding.userPassword.text.toString().trim()


if (userPasswordText!!.isEmpty()){
        binding.userPassword.setHint(R.string.password_empty_hint_txt)
        binding.userPasswordError.visibility = View.GONE
        binding.userPassword.setBackgroundResource(R.drawable.inpute_wrong_backround)
    desnableConPassword()
        userPasswordIsRight = false
}else if (!userPasswordText!!.matches(passwordRegex)){
    binding.userPasswordError.setText(R.string.pass_word_matching)
    binding.userPasswordError.visibility = View.VISIBLE
    binding.userPassword.setBackgroundResource(R.drawable.inpute_wrong_backround)
    userPasswordIsRight = false
    desnableConPassword()
}

else{
    binding.userPasswordError.visibility = View.GONE
    binding.userPassword.setBackgroundResource(R.drawable.white_input_text_backround_res)
    userPasswordIsRight = true
    enableConPassword()
    enableRgisterBtn()

}
        }
        binding.userConPassword.addTextChangedListener {
            val conPasswordTxt = binding.userConPassword.text.toString()
            val originalPasswordTxt = binding.userPassword.text.toString()  // Assuming this is the original password

            if (conPasswordTxt.isEmpty()) {
                binding.userConPassword.setHint(R.string.conPasword_empty_hint_txt)
                binding.userConPasswordError.visibility = View.GONE
                binding.userConPassword.setBackgroundResource(R.drawable.inpute_wrong_backround)
                userConPasswordIsRight = false
                enableRgisterBtn()
            } else if (conPasswordTxt != originalPasswordTxt) {
                binding.userConPasswordError.setText(R.string.password_not_matching_text)
                binding.userConPasswordError.visibility = View.VISIBLE
                binding.userConPassword.setBackgroundResource(R.drawable.inpute_wrong_backround)
                userConPasswordIsRight = false
                enableRgisterBtn()
            } else {
                binding.userConPasswordError.visibility = View.GONE
                binding.userConPassword.setBackgroundResource(R.drawable.white_input_text_backround_res)
                userConPasswordIsRight = true
                enableRgisterBtn()
            }
        }
    }
    private fun selectUserSex(){
    binding.maleRadioBtn.setOnClickListener{
        userSex = "male"
    }
    binding.girlRadioBtn.setOnClickListener{
        userSex = "girl"
    }
}
    private fun alertDialog(userId:String){
        val view = layoutInflater.inflate(R.layout.user_card_custom,null)
        var card_user_name = view.findViewById<TextView>(R.id.card_name)
        var card_user_email = view.findViewById<TextView>(R.id.card_email)
        var card_user_sex = view.findViewById<TextView>(R.id.card_sex)
        var card_user_image = view.findViewById<ImageView>(R.id.user_card_image)
        var save_btn = view.findViewById<Button>(R.id.user_card_save_btn)
        var cancel_btn = view.findViewById<Button>(R.id.user_card_cancel_btn)
        dialog= Dialog(this)
        dialog!!.setContentView(view)
        dialog !! .window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        card_user_name.setText(binding.username.text.toString())
        card_user_email.setText(binding.useremail.text.toString())
        card_user_sex.setText(userSex)
        card_user_image.setImageURI(accountImageUri)
        save_btn.setOnClickListener {
            upLoadUserImage(userId,accountImageUri)
            dialog!!.dismiss()
        }
        cancel_btn.setOnClickListener {
            dialog!!.dismiss()
        }
        dialog!!.show()
    }
    private fun upLoadUserImage(userId: String, accountImage: Uri) {
val userImageStorage = FirebaseStorage.getInstance()
        val formate= SimpleDateFormat("yyyy_MM_dd_HH_mm_ss" , Locale.getDefault())
        val date = Date()
        val fileName :String = formate.format(date)
        val categoriesImgRef = userImageStorage.getReference("UsersImages").child(fileName + ".store")
        categoriesImgRef.putFile(accountImage).addOnSuccessListener(object: OnSuccessListener<UploadTask.TaskSnapshot>{
            override fun onSuccess(p0: UploadTask.TaskSnapshot?){
                categoriesImgRef.downloadUrl.addOnSuccessListener{ uri->
                    insertNewUser(userId, uri.toString())
                }
            }

        })


    }
    private fun sucessefulDilaod (){
        val dialogView = layoutInflater.inflate(R.layout.user_card_custom , null)
        visibleView(dialogView.findViewById(R.id.successful_layout), dialogView.findViewById(R.id.card_layout))
        var successDialog = Dialog(this)
        successDialog.setContentView(dialogView)
        successDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        successDialog.setCancelable(false)
        successDialog.show()
        hideDialog(successDialog)
    }
    private fun hideDialog(dialog: Dialog) {
 var handler = Handler()
        var runnable = Runnable {
            dialog.dismiss()
        }
        handler.postDelayed(runnable,1*1000)
        var intent = Intent(baseContext,MainActivity::class.java)
        startActivity(intent)
    }
    private fun enableConPassword(){
        binding.userConPassword.isEnabled = true
        binding.userConPassword.setBackgroundResource(R.drawable.white_input_text_backround_res)
    }
    private fun desnableConPassword(){
        binding.userConPassword.isEnabled = false
        binding.userConPassword.setBackgroundResource(R.drawable.enable_inputes_backround)
    }
    private fun visibleView(visibeleView: RelativeLayout?,goneView: RelativeLayout?) {
        goneView?.visibility = View.GONE
        visibeleView?.visibility = View.VISIBLE
    }
}
