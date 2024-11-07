package com.example.store_application.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.store_application.AppliactionObjs.Accounts
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Database(entities = arrayOf(Accounts::class), version = 1, exportSchema = false)
abstract class StoreRoom : RoomDatabase() {

    abstract fun accountDao(): AccountDao

    companion object {

        @Volatile
        private var INSTANCE: StoreRoom? = null
        const val NUMBER_OF_THREADS: Int = 4
        val databaseWriteExecutor : ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)
        fun getDatabase(context: Context): StoreRoom {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StoreRoom::class.java,
                    "StoreDB"
                ).build()
                INSTANCE = instance
                return instance
            }

        }
    }
}