package com.example.financecompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financecompanion.HomeScreen.*
import com.example.financecompanion.dataModel.model.Transaction
import com.example.financecompanion.viewmodels.VaultProcessor

// Navigation Destinations
enum class Screen { HOME, ACTIVITY, INSIGHT , PROFILE }

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val processor = VaultProcessor()
            val state by processor.uiState.collectAsState()

            var currentScreen by remember { mutableStateOf(Screen.HOME) }

            // States for Forms/Dialogs
            var showAddSheet by remember { mutableStateOf(false) }
            var showTransferDialog by remember { mutableStateOf(false) } // NEW: Transfer State

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
                                onTransferClicked = { showTransferDialog = true } // NEW: Pass click up
                            )
                            Screen.INSIGHT -> InsightsScreen(state = state)
                            Screen.ACTIVITY -> ActivityScreen(state = state)
                            Screen.PROFILE -> ProfileScreen(state = state)
                        }

                        // --- REQUIREMENT 2: ADD TRANSACTION SHEET ---
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

                        // --- REQUIREMENT 3: TRANSFER DIALOG ---
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

// --- NEW COMPONENT: TRANSFER DIALOG ---
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

    Column(Modifier.padding(24.dp).fillMaxWidth()) {
        Text("New Entry", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth())

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
            Switch(checked = isIncome, onCheckedChange = { isIncome = it })
            Spacer(modifier = Modifier.width(8.dp))
            Text("Is this Income?")
        }

        Button(
            onClick = {
                onSave(Transaction(
                    title = title,
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    isIncome = isIncome,
                    category = if(isIncome) "Income" else "General",
                    subtitle = if(isIncome) "Deposit" else "Expense",
                    date = "Today"
                ))
                onDismiss()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Save Transaction") }
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