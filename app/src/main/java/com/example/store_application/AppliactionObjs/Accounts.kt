package com.example.store_application.AppliactionObjs

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
@Entity(tableName = "account")
class Accounts(
  @PrimaryKey
  var email: String,
  var accountuserId :String,
  var accountProfile: String,
  var accountPassword: String,
  var accountName: String,
  var accountLogain :Boolean
):Serializable{
  constructor() : this("","","","" ,"", false)
}
