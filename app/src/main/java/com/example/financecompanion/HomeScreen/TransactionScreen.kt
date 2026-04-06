package com.example.financecompanion.HomeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financecompanion.dataModel.model.Transaction
import com.example.financecompanion.dataModel.model.ViewModelState
import com.example.financecompanion.viewmodels.FinanceCompanionViewModel

@Composable
fun FinanceCompanionTransactionScreen(
    state: ViewModelState,
    viewModel: FinanceCompanionViewModel,
    currencySymbol: String
) {
    var searchQuery by remember { mutableStateOf("") }
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = { Text("Search transactions...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFF00796B)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Transaction List According to days
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            val filteredEntries = state.recentEntries.filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }

            val todayEntries = filteredEntries.filter { it.date.contains("Today", ignoreCase = true) }
            val yesterdayEntries = filteredEntries.filter { it.date.contains("Yesterday", ignoreCase = true) }
            val otherEntries = filteredEntries.filter {
                !it.date.contains("Today", ignoreCase = true) && !it.date.contains("Yesterday", ignoreCase = true)
            }

            // Today
            if (todayEntries.isNotEmpty()) {
                item { SectionHeader("TODAY") }
                items(todayEntries) { entry ->
                    TransactionMenu(entry, viewModel, currencySymbol, onEdit = { transactionToEdit = it })
                }
            }

            // Yesterday
            if (yesterdayEntries.isNotEmpty()) {
                item { SectionHeader("YESTERDAY") }
                items(yesterdayEntries) { entry ->
                    TransactionMenu(entry, viewModel, currencySymbol, onEdit = { transactionToEdit = it })
                }
            }

            // Previous
            if (otherEntries.isNotEmpty()) {
                item { SectionHeader("PREVIOUS") }
                items(otherEntries) { entry ->
                    TransactionMenu(entry, viewModel, currencySymbol, onEdit = { transactionToEdit = it })
                }
            }

            if (state.recentEntries.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No transactions found", color = Color.Gray)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Edit transaction
    transactionToEdit?.let { tx ->
        EditTransactionDialog(
            transaction = tx,
            onDismiss = { transactionToEdit = null },
            onConfirm = { updatedTx ->
                viewModel.updateTransaction(updatedTx)
                transactionToEdit = null
            }
        )
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        ),
        color = Color.Gray,
        modifier = Modifier
            .padding(top = 20.dp, bottom = 8.dp)
            .fillMaxWidth()
    )
}

@Composable
fun TransactionMenu(
    entry: Transaction,
    processor: FinanceCompanionViewModel,
    currencySymbol: String, // Pass symbol through
    onEdit: (Transaction) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            onClick = { showMenu = true },
            shape = RoundedCornerShape(16.dp),
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
            TransactionRow(entry, currencySymbol)
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp)) },
                onClick = {
                    showMenu = false
                    onEdit(entry)
                }
            )
            DropdownMenuItem(
                text = { Text("Delete", color = Color.Red) },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp)) },
                onClick = {
                    showMenu = false
                    processor.deleteTransaction(entry)
                }
            )
        }
    }
}

@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onConfirm: (Transaction) -> Unit
) {
    var title by remember { mutableStateOf(transaction.title) }
    var amount by remember { mutableStateOf(transaction.amount.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Transaction", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val valAmount = amount.toDoubleOrNull() ?: transaction.amount
                    onConfirm(transaction.copy(title = title, amount = valAmount))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
            ) { Text("Update") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}