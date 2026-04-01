package com.example.financecompanion.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import com.example.financecompanion.dataModel.DataStorage.FinanceRepository
import com.example.financecompanion.dataModel.model.Budgetlist
import com.example.financecompanion.dataModel.model.Dataledger
import com.example.financecompanion.dataModel.model.Transaction
import com.example.financecompanion.dataModel.model.VaultState
import kotlinx.coroutines.flow.update

class VaultProcessor(private val repository: FinanceRepository = FinanceRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow(VaultState())
    val uiState = _uiState.asStateFlow()

    init { updateState() }

    fun updateState() {
        val list = repository.entries
        val income = list.filter { it.isIncome }.sumOf { it.amount }
        val expenses = list.filter { !it.isIncome }.sumOf { it.amount }

        _uiState.update { it.copy(
            recentEntries = list,
            totalIncome = income,
            totalExpenses = expenses,
            balance = income - expenses
        )}
    }

    fun addTransaction(t: Transaction) {
        repository.add(t)
        updateState()
    }

    fun removeTransaction(id: String) {
        repository.delete(id)
        updateState()
    }

    fun performTransfer(amount: Double, goalName: String) {
        // 1. Create a transaction that marks the money as "moved"
        val transferTx = Transaction(
            title = "Transfer to $goalName",
            amount = amount,
            category = "Savings",
            date = "Today",
            subtitle = "Internal Transfer", // Fixed your subtitle error here!
            isIncome = false // It's an "expense" from the main balance
        )

        // 2. Save it
        addTransaction(transferTx)

        // 3. Logic to update your Savings Goal progress
        // (You would update a separate 'currentSavings' variable in your state)
    }
}

