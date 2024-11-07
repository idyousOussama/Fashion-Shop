package com.example.store_application.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.store_application.Adapters.CategoriesAdapter.categoriesCustomViewHolder
import com.example.store_application.AppliactionObjs.Category
import com.example.store_application.Listeners.CategoryItemListener
import com.example.store_application.Listeners.CategoryListener
import com.example.store_application.R
import com.squareup.picasso.Picasso

class CategoriesAdapter (): RecyclerView.Adapter<categoriesCustomViewHolder>() {
     var lastItemViewTxet : TextView? = null
     lateinit var itemListener : CategoryItemListener
     var currentItem : TextView? = null
    var categoryList: ArrayList<Category> =ArrayList()
    fun setListener(listener: CategoryItemListener){
        this.itemListener = listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): categoriesCustomViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.main_category_custom, viewGroup, false)
        return categoriesCustomViewHolder(v)
    }

    override fun onBindViewHolder(categoriesCustomViewHolder: categoriesCustomViewHolder, i: Int) {
        val category = categoryList[i]
        categoriesCustomViewHolder.bind(category.categoryName, category.categoryImage)
        categoriesCustomViewHolder.currentItemView.setOnClickListener {
            lastItemViewTxet = currentItem
            itemListener.onItemViewClicked(categoriesCustomViewHolder.categoryName,category)
            categoriesCustomViewHolder.categoryName.setTextColor(categoriesCustomViewHolder.currentItemView.context.resources.getColor(R.color.brow))
            lastItemViewTxet?.setTextColor(categoriesCustomViewHolder.currentItemView.context.resources.getColor(R.color.black))
            currentItem = categoriesCustomViewHolder.categoryName


        }

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class categoriesCustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImage: ImageView =
            itemView.findViewById(R.id.category_image_custom)
         val categoryName: TextView =
            itemView.findViewById(R.id.category_name_custom)
        var currentItemView = itemView

        fun bind(catName: String?, catImage: String) {
            categoryName.text = catName
          Picasso.get().load(catImage.toUri()).placeholder(R.drawable.place_holder_image).into(categoryImage)
        }
    }
}
