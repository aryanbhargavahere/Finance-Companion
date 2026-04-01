package com.example.financecompanion.dataModel.model

import androidx.compose.ui.graphics.Color

data class Budgetlist(
    val title: String,
    val current: Double,
    val max: Double,
    val transactions: Int,
    val status: String,
    val color: Color
)