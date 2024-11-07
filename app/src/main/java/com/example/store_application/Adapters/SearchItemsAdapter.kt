package com.example.store_application.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.store_application.AppliactionObjs.Product
import com.example.store_application.Listeners.SearchItemListener
import com.example.store_application.R
import com.squareup.picasso.Picasso

class SearchItemsAdapter : RecyclerView.Adapter<SearchItemsAdapter.SearchItemsCustomViewHolder>() {

 var searchItemList :ArrayList<Product> = ArrayList()
 lateinit var listener : SearchItemListener


 fun setItemListener (listener : SearchItemListener){
     this.listener = listener
 }
    fun setSearchList(searchItemList :ArrayList<Product>) {
        this.searchItemList = searchItemList
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SearchItemsCustomViewHolder {
        var view = LayoutInflater.from(p0.context).inflate(R.layout.search_item_custom,p0,false)
        return SearchItemsCustomViewHolder(view)
    }
    override fun getItemCount(): Int {
return   searchItemList.size
    }
    override fun onBindViewHolder(p0: SearchItemsCustomViewHolder, p1: Int) {
var product = searchItemList.get(p1)
        p0.getBind(product.productName,product.productPrice.toString(),product.productImage,product.likesNum.toString())
   p0.itemView.setOnClickListener{
       listener.onItemClicked(product)
   }

    }
    class SearchItemsCustomViewHolder(itemView: View) :ViewHolder(itemView){
       private  var itemImage = itemView.findViewById<ImageView>(R.id.search_item_Img)
        private var itemName = itemView.findViewById<TextView>(R.id.search_item_Name)
        private var itemPrice = itemView.findViewById<TextView>(R.id.search_item_Price)
        private var itemLikes = itemView.findViewById<TextView>(R.id.likesnum_item_Price)

fun getBind(proName: String , proPrice :String , proImage:String, productLikes : String){
    Picasso
        .get().load(proImage.toUri()).placeholder(R.drawable.place_holder_image).into(itemImage)
itemName.setText(proName)
itemName.setText(productLikes)
    itemPrice.setText(proPrice)
}

    }
}