package com.example.store_application.Adapters

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.store_application.R
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.net.toUri
import com.example.store_application.AppliactionObjs.Product
import com.example.store_application.AppliactionObjs.User
import com.example.store_application.Listeners.ProductItemListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductAdapter ( ) : RecyclerView.Adapter<ProductAdapter.ProductCustomViewHolder>() {
    var productsList :ArrayList<Product> = ArrayList()
    lateinit var listener : ProductItemListener
    val firebaseDB = FirebaseDatabase.getInstance()
    var  currentUsr : User? = null
fun setItemLestener (listener : ProductItemListener ){
    this.listener = listener

notifyDataSetChanged()
}
    fun setProductList (proLis:ArrayList<Product>,  currentUsr : User){
        productsList = proLis
        this. currentUsr =   currentUsr
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ProductCustomViewHolder {
    val v = LayoutInflater.from(p0.context).inflate(R.layout.main_product_custom,p0,false)
   return ProductCustomViewHolder(v)
    }


    override fun getItemCount(): Int {
        return productsList.size
   }

    override fun onBindViewHolder(p0: ProductCustomViewHolder, p1: Int) {
        var product : Product  = productsList[p1]
        val productRef = firebaseDB.getReference("ProductLikers")
        productRef.child(product.productId).orderByKey().equalTo(currentUsr!!.userId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    p0.bind(product.productName,product.productPrice.toString() , product.productImage,true , product.likesNum)
                }else{
                    p0.bind(product.productName,product.productPrice.toString() , product.productImage,false, product.likesNum)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        p0.item.setOnClickListener {
listener.onItemClicked(product)
        }
        p0.productLove!!.setOnClickListener{
            p0.productLove.setImageResource(R.drawable.fill_heart)
            p0.productLove.isEnabled = false
            p0.likeNumber!!.setText((product.likesNum+1).toString())
            listener.onLikedProductListner(product)
        }
    }

    class ProductCustomViewHolder(itemView: View) : ViewHolder(itemView) {
        val item = itemView
        val productImg :ImageView? = itemView.findViewById(R.id.product_custom_img)
        val productName : TextView? =  itemView.findViewById(R.id.product_costom_name)
        val  productPrice : TextView? = itemView.findViewById(R.id.product_costom_price)
        val  productLove : ImageView? = itemView.findViewById(R.id.liked_Product)
        val  likeNumber : TextView? = itemView.findViewById(R.id.product_costom_like)
        fun bind (proName: String, proPrice: String, proImage: String , liked : Boolean , likeNum : Int ){
                    Picasso.get().load(proImage.toUri()).placeholder(R.drawable.place_holder_image).into(productImg)
                productName?.setText(proName)
                productPrice?.setText(proPrice)
                productName?.setText(proName)
                productPrice?.setText(proPrice.toString())
            likeNumber!!.setText(likeNum.toString())
            if(liked){
                productLove!!.setImageResource(R.drawable.fill_heart)
                productLove!!.isEnabled = false

            }else{
                productLove!!.setImageResource(R.drawable.empty_heart)

            }
        }




    }
}
