package com.example.store_application.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.store_application.AppliactionObjs.Accounts

@Dao
interface AccountDao {

@Insert
fun insertNewAccount (account : Accounts)
@Query("Select * From account")
fun getAllAccount() : LiveData<List<Accounts>>
    @Query("SELECT * FROM account WHERE accountLogain = 1")
    fun getLogainedAccount(): Accounts
    @Query("SELECT*FROM account WHERe account.email = :email")
    fun getAccountByEmail(email :String):Accounts

    @Query("UPDATE account SET accountLogain = :isLogained WHERE email = :accountEmail")
    fun updateAccountLogainStatus(accountEmail: String, isLogained: Boolean)

}