package com.example.store_application.Adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.store_application.AppliactionObjs.Category
import com.example.store_application.Listeners.CategoryListener
import com.example.store_application.R
import com.squareup.picasso.Picasso

class DialogCategoriesAdapter (var listener : CategoryListener) :
    RecyclerView.Adapter<DialogCategoriesAdapter.DialogCustomViewHolder>() {
    var categoriesList: ArrayList<Category> = ArrayList()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DialogCustomViewHolder {
    var view = LayoutInflater.from(p0.context).inflate(R.layout.dialod_category_custom, p0 , false)
    return DialogCustomViewHolder(view)
    }
  fun setCategoryList (categoriesList :   ArrayList<Category>){
     this.categoriesList = categoriesList
  }

    override fun getItemCount(): Int {
return        categoriesList.size    }

    override fun onBindViewHolder(p0: DialogCustomViewHolder, p1: Int) {
    var category = categoriesList.get(p1)
        p0.setInfo(Uri.parse(category.categoryImage) , category.categoryName)
        p0.itemViews.setOnClickListener {
listener.onCategoyNameClecked(category.categoryName)
        }

    }

    class DialogCustomViewHolder(itemView: View) : ViewHolder(itemView) {
var categoryImage = itemView.findViewById<ImageView>(R.id.dialog_image_category_custom)
var categoryName = itemView.findViewById<TextView>(R.id.dialog_name_category_custom)
        var itemViews = itemView
fun setInfo (imageUri:Uri , cateame: String){
    Picasso.get().load(imageUri).placeholder(R.drawable.place_holder_image).into(categoryImage)
    categoryName.setText(cateame)

}
    }

}