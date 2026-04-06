package com.example.financecompanion.dataModel.model

data class ViewModelState(
    val recentEntries: List<Transaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val totalSavings: Double = 0.0,
    val balance: Double = 0.0,
    val categoryTotals: Map<String, Double> = emptyMap()
)