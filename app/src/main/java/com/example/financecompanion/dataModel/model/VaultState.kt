package com.example.financecompanion.dataModel.model

data class VaultState(
    val recentEntries: List<Transaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val totalSavings: Double = 0.0, // Added for your real-time goal
    val balance: Double = 0.0,
    val categoryTotals: Map<String, Double> = emptyMap()
)