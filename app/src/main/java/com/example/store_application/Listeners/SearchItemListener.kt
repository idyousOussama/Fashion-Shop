package com.example.store_application.Listeners

import com.example.store_application.AppliactionObjs.Product

interface SearchItemListener {
    fun onItemClicked(product : Product)
}