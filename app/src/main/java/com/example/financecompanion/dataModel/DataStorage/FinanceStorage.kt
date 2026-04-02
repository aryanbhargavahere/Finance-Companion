package com.example.financecompanion.dataModel.DataStorage

import android.view.SurfaceControl
import androidx.compose.runtime.mutableStateListOf
import com.example.financecompanion.dataModel.model.Dataledger
import com.example.financecompanion.dataModel.model.Transaction

class FinanceRepository {
    private val _entries = mutableStateListOf<Transaction>()
    val entries: List<Transaction> get() = _entries

    init {
        // Corrected Mock Data Types
        _entries.add(Transaction(id = 1, title = "Starbucks", amount = 6.50, category = "Dining", date = "Today", subtitle = "Coffee", isIncome = false))
        _entries.add(Transaction(id = 2, title = "Salary", amount = 4500.0, category = "Income", date = "Yesterday", subtitle = "Monthly Pay", isIncome = true))
    }

    fun add(transaction: Transaction) {
        _entries.add(0, transaction)
    }

    // Fix: id is an Int in the Entity, so we compare Ints
    fun delete(id: Int) {
        _entries.removeAll { it.id == id }
    }
}