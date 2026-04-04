package com.example.financecompanion

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
import com.example.financecompanion.HomeScreen.*
import com.example.financecompanion.ProfileInScreens.AppearanceChangeScreen
import com.example.financecompanion.dataModel.model.Transaction
import com.example.financecompanion.ui.theme.FinanceCompanionTheme
import com.example.financecompanion.viewmodels.VaultProcessor

enum class Screen { HOME, ACTIVITY, INSIGHT, PROFILE, APPEARANCE }

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val processor = remember { VaultProcessor(context) }
            val state by processor.uiState.collectAsState()

            // Global UI States
            var currentScreen by remember { mutableStateOf(Screen.HOME) }
            var isDarkMode by remember { mutableStateOf(false) }
            var showAddSheet by remember { mutableStateOf(false) }
            var showTransferDialog by remember { mutableStateOf(false) }

            // Use your new custom theme file
            FinanceCompanionTheme(darkTheme = isDarkMode) {
                Scaffold(
                    topBar = {
                        // Hide TopBar on the Appearance screen for a cleaner settings look
                        if (currentScreen != Screen.APPEARANCE) NexusTopBar()
                    },
                    bottomBar = {
                        // Hide BottomBar on the Appearance screen to focus on settings
                        if (currentScreen != Screen.APPEARANCE) {
                            NexusBottomNavigation(
                                activeTab = currentScreen,
                                onTabSelected = { selection -> currentScreen = selection }
                            )
                        }
                    },
                    // Use theme-based background instead of hardcoded hex
                    containerColor = MaterialTheme.colorScheme.background
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
                            Screen.PROFILE -> ProfileScreen(
                                state = state,
                                // Navigation callback for the Appearance button
                                onNavigateToAppearance = { currentScreen = Screen.APPEARANCE }
                            )
                            Screen.APPEARANCE -> AppearanceChangeScreen(
                                isDarkMode = isDarkMode,
                                onThemeToggle = { isDarkMode = it },
                                onBack = { currentScreen = Screen.PROFILE }
                            )
                        }

                        if (showAddSheet) {
                            ModalBottomSheet(
                                onDismissRequest = { showAddSheet = false },
                                containerColor = MaterialTheme.colorScheme.surface
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
                                    processor.performTransfer(amount, "Add to Savings")
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
        title = { Text("Add to Savings", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Enter the amount to move to your savings vault.", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
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
            ) { Text("Add to Savings") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(onSave: (Transaction) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }

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
            Text(if (isIncome) "Income" else "Expenses")
        }

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
                    id = 0,
                    title = title,
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    category = if (isIncome) "Salary" else selectedCategory,
                    isIncome = isIncome,
                    subtitle = if (isIncome) "Credit" else "Debit",
                    date = "Today"
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexusTopBar() {
    CenterAlignedTopAppBar(
        title = { Text("Finance Companion", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, fontSize = 18.sp), color = MaterialTheme.colorScheme.onBackground) },
        navigationIcon = {
            Box(modifier = Modifier.padding(start = 16.dp).size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            }
        },
        actions = {
            IconButton(onClick = {}) { Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground) }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
fun NexusBottomNavigation(activeTab: Screen, onTabSelected: (Screen) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        val navItems = listOf(
            Triple(Screen.HOME, "HOME", Icons.Default.Home),
            Triple(Screen.ACTIVITY, "ACTIVITY", Icons.Default.BarChart),
            Triple(Screen.INSIGHT, "INSIGHTS", Icons.Default.PieChart),
            Triple(Screen.PROFILE, "PROFILE", Icons.Default.Person)
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