package com.example.financecompanion.dataModel.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val currency: String ="USD",
    val category: String,
    val date: String,
    val subtitle: String,
    val isIncome: Boolean
)

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val id: Int = 1,
    val monthlyGoal: Double = 0.0
)