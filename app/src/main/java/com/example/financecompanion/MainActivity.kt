package com.example.financecompanion

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.financecompanion.HomeScreen.*
import com.example.financecompanion.dataModel.model.Transaction
import com.example.financecompanion.viewmodels.VaultProcessor
import androidx.lifecycle.ViewModelProvider

enum class Screen { HOME, ACTIVITY, INSIGHT , PROFILE }

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // FIX: Use a Factory to provide the Application context to the Room Database
            val context = LocalContext.current
            val processor = remember { VaultProcessor(context) }
            val state by processor.uiState.collectAsState()
            var currentScreen by remember { mutableStateOf(Screen.HOME) }
            var showAddSheet by remember { mutableStateOf(false) }
            var showTransferDialog by remember { mutableStateOf(false) }

            MaterialTheme {
                Scaffold(
                    topBar = { NexusTopBar() },
                    bottomBar = {
                        NexusBottomNavigation(
                            activeTab = currentScreen,
                            onTabSelected = { selection -> currentScreen = selection }
                        )
                    },
                    containerColor = Color(0xFFF8FAFC)
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (currentScreen) {
                            Screen.HOME -> HomeDashboard(
                                state = state,
                                onAddTransactionClicked = { showAddSheet = true },
                                onTransferClicked = { showTransferDialog = true },
                                onInsightsClicked = { currentScreen = Screen.INSIGHT }
                            )
                            Screen.INSIGHT -> InsightsScreen(state = state)
                            Screen.ACTIVITY -> ActivityScreen(state = state)
                            Screen.PROFILE -> ProfileScreen(state = state)
                        }

                        if (showAddSheet) {
                            ModalBottomSheet(
                                onDismissRequest = { showAddSheet = false },
                                containerColor = Color.White
                            ) {
                                AddTransactionSheet(
                                    onSave = { newTx ->
                                        processor.addTransaction(newTx)
                                        showAddSheet = false
                                    },
                                    onDismiss = { showAddSheet = false }
                                )
                            }
                        }

                        if (showTransferDialog) {
                            TransferDialog(
                                onConfirm = { amount ->
                                    processor.performTransfer(amount, "Savings Vault")
                                    showTransferDialog = false
                                },
                                onDismiss = { showTransferDialog = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun TransferDialog(onConfirm: (Double) -> Unit, onDismiss: () -> Unit) {
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Move to Savings", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Enter the amount to transfer to your savings vault.", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = amount.toDoubleOrNull() ?: 0.0
                    if (value > 0) onConfirm(value)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
            ) { Text("Transfer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddTransactionSheet(onSave: (Transaction) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }

    // Define categories based on your project goals
    val categories = listOf("Food", "Entertainment", "Shopping", "Transport", "Health", "Other")
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    Column(Modifier.padding(24.dp).fillMaxWidth().navigationBarsPadding()) {
        Text("New Entry", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
            Switch(checked = isIncome, onCheckedChange = { isIncome = it })
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isIncome) "Income Source" else "Expenses")
        }

        // Category Picker Implementation
        if (!isIncome) {
            Text("Category", style = MaterialTheme.typography.labelLarge)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSave(Transaction(
                    // Note: id = 0 allows Room to auto-generate the ID
                    id = 0,
                    title = title,
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    // Use "Income" as category if isIncome is true, else use selected
                    category = if (isIncome) "Salary" else selectedCategory,
                    isIncome = isIncome,
                    subtitle = if (isIncome) "Credit" else "Debit",
                    date = "Today" // You can replace this with a real Date formatter later
                ))
                onDismiss()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = title.isNotBlank() && amount.isNotBlank()
        ) {
            Text("Save Transaction")
        }
    }
}

// --- TOP BAR & BOTTOM NAV REMAIN THE SAME AS YOUR PREVIOUS CODE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexusTopBar() {
    CenterAlignedTopAppBar(
        title = { Text("Sovereign Ledger", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, fontSize = 18.sp), color = Color(0xFF0A2540)) },
        navigationIcon = {
            Box(modifier = Modifier.padding(start = 16.dp).size(36.dp).clip(CircleShape).background(Color(0xFFE2E8F0)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF4A5568), modifier = Modifier.size(20.dp))
            }
        },
        actions = {
            IconButton(onClick = {}) { Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF0A2540)) }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFF8FAFC))
    )
}

@Composable
fun NexusBottomNavigation(activeTab: Screen, onTabSelected: (Screen) -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp, modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))) {
        val navItems = listOf(
            Triple(Screen.HOME, "HOME", Icons.Default.Home),
            Triple(Screen.ACTIVITY, "ACTIVITY", Icons.Default.BarChart),
            Triple(Screen.INSIGHT, "INSIGHTS", Icons.Default.PieChart),
            Triple(Screen.PROFILE, "PROFILE", Icons.Default.PersonOutline)
        )
        navItems.forEach { (screen, label, icon) ->
            NavigationBarItem(
                selected = activeTab == screen,
                onClick = { onTabSelected(screen) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Black) },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF00796B), selectedTextColor = Color(0xFF00796B), unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent)
            )
        }
    }
}