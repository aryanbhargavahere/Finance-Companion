package com.example.financecompanion.dataModel.model

data class VaultState(
    val balance: Double = 0.0,        // Changed to Double
    val totalIncome: Double = 0.0,    // Added this
    val totalExpenses: Double = 0.0,  // Added this
    val recentEntries: List<Transaction> = emptyList() // Changed to Transaction
)