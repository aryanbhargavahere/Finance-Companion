package com.example.financecompanion.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financecompanion.dataModel.model.Transaction
import com.example.financecompanion.dataModel.model.UserPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAll(): Flow<List<Transaction>>

    @Insert
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Update
    suspend fun update (transaction: Transaction)

    @Query("SELECT monthlyGoal FROM user_preferences WHERE id = 1")
    fun getMonthlyGoal(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMonthlyGoal(prefs: UserPreferences)
}
