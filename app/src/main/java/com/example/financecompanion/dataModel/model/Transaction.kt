package com.example.financecompanion.dataModel.model

import android.icu.util.Currency
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