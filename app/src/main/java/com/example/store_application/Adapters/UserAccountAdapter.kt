package com.example.store_application.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.store_application.AppliactionObjs.Accounts
import com.example.store_application.Listeners.UserAccountListener
import com.example.store_application.R
import com.squareup.picasso.Picasso

class UserAccountAdapter:RecyclerView.Adapter<UserAccountAdapter.UserAccountViewHolder>() {
  var userAccountList:ArrayList<Accounts> = ArrayList()
lateinit var userAccountItemListner :UserAccountListener
fun setUserAccoutsLis(accountList :List<Accounts>){
    userAccountList = accountList as ArrayList<Accounts>
    notifyDataSetChanged()
}

    fun setUserAccountItemListener(listener :UserAccountListener ){
        userAccountItemListner = listener
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): UserAccountViewHolder {
val view = LayoutInflater.from(p0.context).inflate(R.layout.user_accounts_custom,p0,false)
       return UserAccountViewHolder(view)
    }

    override fun getItemCount(): Int {
       return userAccountList.size
    }

    override fun onBindViewHolder(p0: UserAccountViewHolder, p1: Int) {
        val account = userAccountList.get(p1)
        p0.setUserAccount(account.accountName , account.accountProfile ,account.accountLogain)
        p0.itemView.setOnClickListener{
            if (!account.accountLogain){
                userAccountItemListner.UserAccountItemClicked(account.email)
            }else{
                Toast.makeText(p0.itemView.context , "Account Already Actived", Toast.LENGTH_SHORT).show()
            }
        }
    }

    class UserAccountViewHolder(itemView: View) :ViewHolder(itemView){
private val accuntImage = itemView.findViewById<ImageView>(R.id.user_account_Image)
       private val  accountName = itemView.findViewById<TextView>(R.id.user_account_name)
       private val  activeDot = itemView.findViewById<ImageView>(R.id.acount_active_dot)
       private val  activeText = itemView.findViewById<TextView>(R.id.active_text)

        fun setUserAccount(name :String , image:String,isLogained : Boolean){
       Picasso.get().load(image.toUri()).placeholder(R.drawable.user_place_holder).into(accuntImage)
            accountName.setText(name)
            if(isLogained){
                activeDot.visibility = View.VISIBLE
                activeText.visibility = View.VISIBLE
                itemView.setBackgroundResource(R.drawable.actived_account_backround)
            }else{
                activeDot.visibility = View.GONE
                activeText.visibility = View.GONE
                itemView.setBackgroundResource(R.drawable.white_input_text_backround_res)
            }
        }


    }
}