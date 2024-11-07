package com.example.store_application.RoomDatabase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.store_application.AppliactionObjs.Accounts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoreViewModele(application: Application) : AndroidViewModel(application) {
    val storeRepo = StoreRepository(application)

    suspend fun insertNewAccount(account :Accounts){
        withContext(Dispatchers.IO) {
            storeRepo.insertNewAccount(account)
        }

    }

    suspend fun getAccountByEmail(email :String):Accounts{
        return withContext(Dispatchers.IO) {
            storeRepo.getAccountByEmail(email)
        }

    }


    suspend fun updateAccountLogainStatus(accountEmail: String, isLogained: Boolean){
        withContext(Dispatchers.IO) {
            storeRepo.updateAccountLogainStatus(accountEmail,isLogained)
        }

    }


    suspend fun getAllAccounts() : LiveData<List<Accounts>>{
        return withContext(Dispatchers.IO) {
            storeRepo.getAllAccounts()
        }

    }

    suspend fun getLogainedAccount ()  : Accounts {
        return withContext(Dispatchers.IO) {
            storeRepo.getLogainedAccount()
        }


    }}