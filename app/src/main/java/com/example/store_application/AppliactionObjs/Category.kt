package com.example.store_application.AppliactionObjs

import com.google.firebase.database.DatabaseReference
import java.io.Serializable

class  Category(var categoryName: String = "", var categoryImage: String = "", var categoryId: String = "")  {
    // No-argument constructor required by Firebase
    constructor() : this("", "", "")
}
