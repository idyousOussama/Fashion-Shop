
package com.example.store_application

import android.accounts.Account
import android.app.Dialog
import android.app.VoiceInteractor
import android.content.Intent
import android.os.Bundle
import android.renderscript.Sampler.Value
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.store_application.AppliactionObjs.Accounts
import com.example.store_application.AppliactionObjs.User
import com.example.store_application.RoomDatabase.StoreViewModele
import com.example.store_application.databinding.ActivitySplachBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.IOException
import java.net.HttpURLConnection
import java.net.URL

class Splach_Activity : AppCompatActivity() {
    lateinit var binding: ActivitySplachBinding
    private val firebaseDB= FirebaseDatabase.getInstance()
private     var viewModel : StoreViewModele? = null
    private var account : Accounts? = null
    private var user:User?= null
    private var support :User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplachBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(StoreViewModele::class.java)
        CoroutineScope(Dispatchers.Main).launch{
            account = viewModel!!.getLogainedAccount()
        if(account != null){
            var usersRef = firebaseDB.getReference("Users")
            usersRef.child(account!!.accountuserId).addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
if (p0.exists()){
     user=p0.getValue(User::class.java)
    if (user != null){
       getSupport(user!!)

    }
}else{
    Toast.makeText(baseContext,R.string.account_Removed,Toast.LENGTH_SHORT).show()
    var sginInIntent = Intent(baseContext,SingIn_Activity::class.java)
    startActivity(sginInIntent)
    finish()
}
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })
        }else{
            val intent = Intent(baseContext,SingIn_Activity::class.java)
            startActivity(intent)
            finish()
        }

        }
    }

    private fun getSupport(user: User) {
        var supportRef = firebaseDB.getReference("Supports")
        supportRef.orderByValue().addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    for (items in p0.children)
                    {
                        support = items.getValue(User :: class.java)
                    }
                    if (support != null && user != null)
                    {
                        val intent = Intent(baseContext,MainActivity::class.java)
                        intent.putExtra("currentUser",user)
                        intent.putExtra("SupportsUser",support)
                        startActivity(intent)
                        finish()
                    }
                    }


                }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}


