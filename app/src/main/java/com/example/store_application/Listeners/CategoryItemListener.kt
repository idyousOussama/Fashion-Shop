package com.example.store_application.Listeners

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import com.example.store_application.AppliactionObjs.Category

interface CategoryItemListener {
    fun onItemViewClicked(lastIteViewText :TextView,category :Category)
}