package com.example.store_application.RoomDatabase

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.store_application.AppliactionObjs.Accounts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoreRepository(application : Application) {

    val StoreLocalDB = StoreRoom.getDatabase(application)

val accountDao = StoreLocalDB.accountDao()

suspend fun insertNewAccount(account :Accounts){
    withContext(Dispatchers.IO) {
        accountDao.insertNewAccount(account)
    }

}
    suspend fun getAccountByEmail(email :String):Accounts{
        return  withContext(Dispatchers.IO) {
            accountDao.getAccountByEmail(email)
        }

    }

    suspend fun updateAccountLogainStatus(accountEmail: String, isLogained: Boolean){
        withContext(Dispatchers.IO) {
            accountDao.updateAccountLogainStatus(accountEmail,isLogained)
        }

    }
suspend fun getAllAccounts() : LiveData<List<Accounts>>{
    return withContext(Dispatchers.IO) {
         accountDao.getAllAccount()
    }

}
   suspend fun getLogainedAccount ()  : Accounts{
       return withContext(Dispatchers.IO) {
           accountDao.getLogainedAccount()
       }

   }
}