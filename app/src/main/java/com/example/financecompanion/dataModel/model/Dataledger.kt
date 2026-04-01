package com.example.financecompanion.dataModel.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Dataledger(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: String,
    val icon: ImageVector,
    val category: String,
    val isIncome: Boolean = false
)