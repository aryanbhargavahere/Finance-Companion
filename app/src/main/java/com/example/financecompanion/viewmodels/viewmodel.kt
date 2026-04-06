package com.example.financecompanion.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financecompanion.Room.AppDatabase
import com.example.financecompanion.currency.Currencycalculate
import com.example.financecompanion.dataModel.model.Transaction
import com.example.financecompanion.dataModel.model.UserPreferences
import com.example.financecompanion.dataModel.model.VaultState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VaultProcessor(context: Context) : ViewModel() {

    private val dao = AppDatabase.getDatabase(context).transactionDao()
    private val _selectedCurrency = MutableStateFlow("USD")
    val selectedCurrency = _selectedCurrency.asStateFlow()

    val uiState: StateFlow<VaultState> = dao.getAll()
        .map { list ->

            val currentRate = Currencycalculate.rates[selectedCurrency.value] ?: 1.0

            // Calculate Income
            val income = list.filter { it.isIncome }.sumOf { it.amount * currentRate}

            // Calculate Savings
            val savings = list.filter { it.category == "Savings" }.sumOf { it.amount * currentRate  }

            // Calculate Expenses
            val expenses = list.filter { !it.isIncome && it.category != "Savings" }.sumOf { it.amount }

            // Map totals by category for graphs
            val totalsByCategory = list.filter { !it.isIncome && it.category != "Savings" }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }



            VaultState(
                recentEntries = list,
                totalIncome = income,
                totalExpenses = expenses,
                totalSavings = savings, // Ensure your VaultState data class has this field
                balance = income - (expenses + savings),
                categoryTotals = totalsByCategory
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VaultState()
        )

    fun addTransaction(t: Transaction) {
        viewModelScope.launch {
            dao.insert(t)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            dao.delete(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            dao.update(transaction)
        }
    }

    // Collect the goal directly from the database Flow
    val monthlyGoal: StateFlow<Double> = dao.getMonthlyGoal()
        .map { it ?: 0.0 } // Default to 0.0 if null
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun updateMonthlyGoal(newGoal: Double) {
        viewModelScope.launch {
            dao.saveMonthlyGoal(UserPreferences(monthlyGoal = newGoal))
        }
    }

    fun performTransfer(amount: Double, goalName: String) {
        val transferTx = Transaction(
            title = goalName,
            amount = amount,
            category = "Savings",
            date = "Today",
            subtitle = "Internal Transfer",
            isIncome = false
        )
        addTransaction(transferTx)
    }

}