package com.example.financecompanion.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.financecompanion.dataModel.model.Transaction

@Database(entities = [Transaction::class], version = 2) // UPDATED: Changed from 1 to 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_db"
                )
                    .fallbackToDestructiveMigration() // ADDED: Prevents the crash by recreating the DB
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}