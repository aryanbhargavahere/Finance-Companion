package com.example.financecompanion.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financecompanion.Room.AppDatabase
import com.example.financecompanion.dataModel.model.Transaction
import com.example.financecompanion.dataModel.model.VaultState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Changed to regular ViewModel to avoid Factory complexity in MainActivity
class VaultProcessor(context: Context) : ViewModel() {

    // Access the DAO directly using the provided context
    private val dao = AppDatabase.getDatabase(context).transactionDao()

    // Requirement 4 & 7: Automated state mapping from SQLite to UI
    val uiState: StateFlow<VaultState> = dao.getAll()
        .map { list ->
            val income = list.filter { it.isIncome }.sumOf { it.amount }
            val expenses = list.filter { !it.isIncome }.sumOf { it.amount }

            VaultState(
                recentEntries = list,
                totalIncome = income,
                totalExpenses = expenses,
                balance = income - expenses
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VaultState()
        )

    // Requirement 2: Permanent Data Entry
    fun addTransaction(t: Transaction) {
        viewModelScope.launch {
            dao.insert(t)
        }
    }

    // Requirement 3: Goal/Internal Transfer Logic
    fun performTransfer(amount: Double, goalName: String) {
        val transferTx = Transaction(
            title = "Transfer to $goalName",
            amount = amount,
            category = "Savings",
            date = "Today",
            subtitle = "Internal Transfer",
            isIncome = false
        )
        addTransaction(transferTx)
    }
}