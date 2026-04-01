package com.example.financecompanion.dataModel.model

data class Transaction(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val category: String,
    val date: String,
    val isIncome: Boolean,
    val subtitle: String,
    val notes: String = ""
)