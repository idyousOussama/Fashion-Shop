package com.example.store_application.AppliactionObjs

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class User(

    var userId : String
    ,  var userSex : String ,
    var userAccount:Accounts? ):Serializable{
    constructor() : this("","",null)
}
