package com.example.financecompanion.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financecompanion.Room.AppDatabase
import com.example.financecompanion.currency.Currencycalculator
import com.example.financecompanion.dataModel.model.Transaction
import com.example.financecompanion.dataModel.model.UserPreferences
import com.example.financecompanion.dataModel.model.ViewModelState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FinanceCompanionViewModel(context: Context) : ViewModel() {

    private val dao = AppDatabase.getDatabase(context).transactionDao()
    private val _selectedCurrency = MutableStateFlow("USD")
    val selectedCurrency = _selectedCurrency.asStateFlow()

    val uiState: StateFlow<ViewModelState> = dao.getAll()
        .map { list ->

            val currentRate = Currencycalculator.rates[selectedCurrency.value] ?: 1.0

            // Calculate's Income
            val income = list.filter { it.isIncome }.sumOf { it.amount * currentRate}

            // Calculate's Savings
            val savings = list.filter { it.category == "Savings" }.sumOf { it.amount * currentRate  }

            // Calculate's Expenses
            val expenses = list.filter { !it.isIncome && it.category != "Savings" }.sumOf { it.amount }

            // Map totals by category
            val totalsByCategory = list.filter { !it.isIncome && it.category != "Savings" }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            ViewModelState(
                recentEntries = list,
                totalIncome = income,
                totalExpenses = expenses,
                totalSavings = savings,
                balance = income - (expenses + savings),
                categoryTotals = totalsByCategory
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ViewModelState()
        )

    // To Add a Transaction
    fun addTransaction(t: Transaction) {
        viewModelScope.launch {
            dao.insert(t)
        }
    }

    // To Delete a Transaction
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            dao.delete(transaction)
        }
    }

    // To Edit a transaction
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            dao.update(transaction)
        }
    }

    // User Can Enter There Monthly Saving Goal Will always be fetched from database once stored or updated
    val monthlyGoal: StateFlow<Double> = dao.getMonthlyGoal()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun updateMonthlyGoal(newGoal: Double) {
        viewModelScope.launch {
            dao.saveMonthlyGoal(UserPreferences(monthlyGoal = newGoal))
        }
    }

    // To add Money To Savings
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