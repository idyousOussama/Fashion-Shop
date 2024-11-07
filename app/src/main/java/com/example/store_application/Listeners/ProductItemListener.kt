package com.example.store_application.Listeners

import com.example.store_application.AppliactionObjs.Product

interface ProductItemListener {
fun onItemClicked (product : Product)
fun onLikedProductListner(product:Product)
}