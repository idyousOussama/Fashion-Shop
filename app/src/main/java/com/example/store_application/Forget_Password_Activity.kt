package com.example.store_application

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.store_application.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.FirebaseDatabase

class Forget_Password_Activity : AppCompatActivity() {
    lateinit var binding:ActivityForgetPasswordBinding
var resetEmail : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    getResetEmail()
    setRestEmail()
    }
    private fun getResetEmail() {
        val resetEmailIntent = intent
    resetEmail = resetEmailIntent.getStringExtra("forgetUserEmail").toString()
        binding.ForgetInUseremail.setText(resetEmail)
    }

    private fun setRestEmail() {
        var userAuth = FirebaseAuth.getInstance()


        binding.sendEmail.setOnClickListener {
            initBtn(1)
            userAuth.sendPasswordResetEmail(resetEmail).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(baseContext,"sended",Toast.LENGTH_SHORT).show()

                    val intent = Intent(baseContext,SingIn_Activity::class.java)
                    startActivity(intent)
                    initBtn(2)
                    finish()
                } else {
                    val error = task.exception
                    when (error) {
                        is FirebaseAuthInvalidUserException -> {
                            Toast.makeText(this, "No account found with this email address.", Toast.LENGTH_SHORT).show()
                            initBtn(2)
                        }
                        else -> {
                            Toast.makeText(this, "Failed to send reset email .", Toast.LENGTH_SHORT).show()
                            initBtn(2)
                        }
                    }
            }
        }.addOnFailureListener {
                initBtn(2)
            }
        }
    }

    private fun initBtn(i: Int) {
        if (i==1){
            binding.sendEmail.visibility = View.GONE
            binding.sendEmailProgress.visibility = View.VISIBLE
        }else{
            binding.sendEmail.visibility = View.VISIBLE
            binding.sendEmailProgress.visibility = View.GONE
        }

    }
}



