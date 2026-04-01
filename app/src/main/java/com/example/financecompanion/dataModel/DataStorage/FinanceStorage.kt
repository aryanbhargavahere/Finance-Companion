package com.example.financecompanion.dataModel.DataStorage

import android.view.SurfaceControl
import androidx.compose.runtime.mutableStateListOf
import com.example.financecompanion.dataModel.model.Dataledger
import com.example.financecompanion.dataModel.model.Transaction

class FinanceRepository {
    private val _entries = mutableStateListOf<Transaction>()
    val entries: List<Transaction> get() = _entries

    init {
        // Initial Mock Data
        _entries.add(Transaction("1", "Starbucks", 6.50, "Dining", "Today", false,"today"))
        _entries.add(Transaction("2", "Salary", 4500.0, "Income", "Yesterday", true,"Yesterday"))
    }

    fun add(transaction: Transaction) { _entries.add(0, transaction) }
    fun delete(id: String) { _entries.removeAll { it.id == id } }
}