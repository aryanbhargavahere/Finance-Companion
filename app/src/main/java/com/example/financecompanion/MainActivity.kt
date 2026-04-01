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
            // Proper MVVM initialization using the lifecycle-viewmodel-compose library
            val processor= VaultProcessor()
            val state by processor.uiState.collectAsState()

            // State for screen navigation
            var currentScreen by remember { mutableStateOf(Screen.HOME) }

            // Requirement 2 & 5: State for the Bottom Sheet Form
            var showAddSheet by remember { mutableStateOf(false) }

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
                        // Switcher logic for app screens
                        when (currentScreen) {
                            Screen.HOME -> HomeDashboard(
                                state = state,
                                onAddTransactionClicked = { showAddSheet = true }
                            )
                            Screen.INSIGHT -> InsightsScreen(state = state)
                            Screen.ACTIVITY -> ActivityScreen(state = state)
                            Screen.PROFILE -> ProfileScreen(state = state)
                        }

                        // Requirement 2: Data Entry Flow (The Modal Form)
                        if (showAddSheet) {
                            ModalBottomSheet(
                                onDismissRequest = { showAddSheet = false },
                                sheetState = rememberModalBottomSheetState(),
                                containerColor = Color.White,
                                dragHandle = { BottomSheetDefaults.DragHandle() }
                            ) {
                                // This calls your AddTransactionSheet component
                                AddTransactionSheet(
                                    onSave = { newTx ->
                                        processor.addTransaction(newTx)
                                        showAddSheet = false // Auto-close on success
                                    },
                                    onDismiss = { showAddSheet = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- TOP BAR COMPONENT ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexusTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Sovereign Ledger",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                ),
                color = Color(0xFF0A2540)
            )
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color(0xFF4A5568),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Future Notifications Logic */ }) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF0A2540)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFF8FAFC)
        )
    )
}

// --- BOTTOM NAVIGATION COMPONENT ---
@Composable
fun NexusBottomNavigation(
    activeTab: Screen,
    onTabSelected: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        val navItems = listOf(
            Triple(Screen.HOME, "HOME", Icons.Default.Home),
            Triple(Screen.ACTIVITY, "ACTIVITY", Icons.Default.BarChart),
            Triple(Screen.INSIGHT, "BUDGET", Icons.Default.AccountBalanceWallet),
            Triple(Screen.PROFILE, "PROFILE", Icons.Default.PersonOutline)
        )

        navItems.forEach { (screen, label, icon) ->
            NavigationBarItem(
                selected = activeTab == screen,
                onClick = { onTabSelected(screen) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Black) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF00796B),
                    selectedTextColor = Color(0xFF00796B),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
@Composable
fun AddTransactionSheet(onSave: (Transaction) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }

    Column(Modifier.padding(24.dp).fillMaxWidth()) {
        Text("New Entry", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = isIncome, onCheckedChange = { isIncome = it })
            Text("Is this Income?")
        }

        Button(
            onClick = {
                onSave(Transaction(title = title, amount = amount.toDoubleOrNull() ?: 0.0, isIncome = isIncome, category = "General", subtitle = "",date = "Today"))
                onDismiss()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save Transaction") }
    }
}