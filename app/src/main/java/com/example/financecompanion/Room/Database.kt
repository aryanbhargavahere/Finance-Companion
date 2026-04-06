package com.example.financecompanion.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.financecompanion.dataModel.model.Transaction
import com.example.financecompanion.dataModel.model.UserPreferences

// UPDATED: Added UserPreferences::class to the entities list
@Database(entities = [Transaction::class, UserPreferences::class], version = 3)
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
                    /* Since we added a new table, this will wipe the old 'version 3'
                       database and recreate it with both tables.
                    */
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}