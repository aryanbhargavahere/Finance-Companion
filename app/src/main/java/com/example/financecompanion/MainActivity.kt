package com.example.financecompanion

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import com.example.financecompanion.Authentication.BiometricAuth
import com.example.financecompanion.HomeScreen.*
import com.example.financecompanion.ProfileInScreens.AppearanceChangeScreen
import com.example.financecompanion.ProfileInScreens.Currencychange
import com.example.financecompanion.ProfileInScreens.PersonalInfoScreen
import com.example.financecompanion.dataModel.model.Transaction
import com.example.financecompanion.ui.theme.FinanceCompanionTheme
import com.example.financecompanion.viewmodels.VaultProcessor

enum class Screen { HOME, ACTIVITY, INSIGHT, PROFILE, APPEARANCE, CURRENCY, PERSONAL_INFO }

class MainActivity : FragmentActivity() {

    // --- Persistence Constants ---
    private val PREFS_NAME = "finance_prefs"
    private val KEY_USER_NAME = "user_name"
    private val KEY_USER_CURRENCY = "user_currency"

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val processor = remember { VaultProcessor(context) }
            val state by processor.uiState.collectAsState()
            val monthlyGoal by processor.monthlyGoal.collectAsState()

            // Initialize SharedPreferences for data persistence
            val sharedPrefs = remember { getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

            // --- App State Management ---
            var isAuthenticated by remember { mutableStateOf(false) }
            var currentScreen by remember { mutableStateOf(Screen.HOME) }
            var isDarkMode by remember { mutableStateOf(false) }

            // User Info (Loaded from Storage)
            var userName by remember { mutableStateOf(sharedPrefs.getString(KEY_USER_NAME, "") ?: "") }
            var userCurrency by remember { mutableStateOf(sharedPrefs.getString(KEY_USER_CURRENCY, "USD") ?: "USD") }
            var userCountry by remember { mutableStateOf("India") }
            var userLanguage by remember { mutableStateOf("English") }

            // UI Interaction States
            var showNameOnboarding by remember { mutableStateOf(false) }
            var showCurrencyOnboarding by remember { mutableStateOf(false) }
            var showAddSheet by remember { mutableStateOf(false) }
            var showTransferDialog by remember { mutableStateOf(false) }
            var showGoalDialog by remember { mutableStateOf(false) }

            // Dynamic Symbol Helper
            val currencySymbol = remember(userCurrency) {
                when(userCurrency) {
                    "EUR" -> "€"
                    "GBP" -> "£"
                    "INR" -> "₹"
                    "JPY" -> "¥"
                    else -> "$"
                }
            }

            // --- App Initialization Logic ---

            // Show onboarding only if user is authenticated but name is empty (first time)
            LaunchedEffect(isAuthenticated) {
                if (isAuthenticated && userName.isEmpty()) {
                    showNameOnboarding = true
                }
            }

            // Trigger Biometric Login on startup
            LaunchedEffect(Unit) {
                BiometricAuth.authenticate(this@MainActivity) { success ->
                    isAuthenticated = success
                }
            }

            FinanceCompanionTheme(darkTheme = isDarkMode) {
                if (isAuthenticated) {
                    Scaffold(
                        topBar = {
                            // Only show TopBar on main dashboard screens
                            if (currentScreen !in listOf(Screen.APPEARANCE, Screen.CURRENCY, Screen.PERSONAL_INFO)) {
                                FinanceCompanionTopBar(
                                    onProfileClick = { currentScreen = Screen.PROFILE }
                                )
                            }
                        },
                        bottomBar = {
                            if (currentScreen !in listOf(Screen.APPEARANCE, Screen.CURRENCY, Screen.PERSONAL_INFO)) {
                                FinanceCompanionBottomNavigation(
                                    activeTab = currentScreen,
                                    onTabSelected = { currentScreen = it }
                                )
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.background
                    ) { padding ->
                        Box(modifier = Modifier.padding(padding)) {
                            // --- Navigation Controller ---
                            when (currentScreen) {
                                Screen.HOME -> HomeDashboard(
                                    state = state,
                                    monthlyGoal = monthlyGoal,
                                    currencySymbol = currencySymbol,
                                    onAddTransactionClicked = { showAddSheet = true },
                                    onTransferClicked = { showTransferDialog = true },
                                    onInsightsClicked = { currentScreen = Screen.INSIGHT },
                                    onEditGoalClicked = { showGoalDialog = true }
                                )
                                Screen.INSIGHT -> InsightsScreen(state, currencySymbol)
                                Screen.ACTIVITY -> ActivityScreen(state, processor, currencySymbol)
                                Screen.PROFILE -> ProfileScreen(
                                    state = state,
                                    userName = userName,
                                    onNavigateToAppearance = { currentScreen = Screen.APPEARANCE },
                                    onNavigateToCurrency = { currentScreen = Screen.CURRENCY },
                                    onNavigateToPersonal = { currentScreen = Screen.PERSONAL_INFO }
                                )
                                Screen.APPEARANCE -> AppearanceChangeScreen(
                                    isDarkMode = isDarkMode,
                                    onThemeToggle = { isDarkMode = it },
                                    onBack = { currentScreen = Screen.PROFILE }
                                )
                                Screen.CURRENCY -> Currencychange(
                                    currentCurrency = userCurrency,
                                    onCurrencyChange = {
                                        userCurrency = it
                                        sharedPrefs.edit().putString(KEY_USER_CURRENCY, it).apply()
                                    },
                                    onBack = { currentScreen = Screen.PROFILE }
                                )
                                Screen.PERSONAL_INFO -> PersonalInfoScreen(
                                    userName = userName,
                                    userCountry = userCountry,
                                    userLanguage = userLanguage,
                                    onNameChange = {
                                        userName = it
                                        sharedPrefs.edit().putString(KEY_USER_NAME, it).apply()
                                    },
                                    onCountryChange = { userCountry = it },
                                    onLanguageChange = { userLanguage = it },
                                    onBack = { currentScreen = Screen.PROFILE }
                                )
                            }

                            // --- Onboarding Flow (Persistent) ---

                            if (showNameOnboarding) {
                                OnboardingNameDialog { name ->
                                    userName = name
                                    sharedPrefs.edit().putString(KEY_USER_NAME, name).apply()
                                    showNameOnboarding = false
                                    showCurrencyOnboarding = true
                                }
                            }

                            if (showCurrencyOnboarding) {
                                OnboardingCurrencyDialog { currency ->
                                    userCurrency = currency
                                    sharedPrefs.edit().putString(KEY_USER_CURRENCY, currency).apply()
                                    showCurrencyOnboarding = false
                                }
                            }

                            // --- Global Dialog Overlays ---

                            if (showAddSheet) {
                                ModalBottomSheet(onDismissRequest = { showAddSheet = false }) {
                                    AddTransaction(
                                        onSave = { processor.addTransaction(it); showAddSheet = false },
                                        onDismiss = { showAddSheet = false }
                                    )
                                }
                            }

                            if (showTransferDialog) {
                                TransferDialog(
                                    onConfirm = { amt -> processor.performTransfer(amt, "Savings"); showTransferDialog = false },
                                    onDismiss = { showTransferDialog = false }
                                )
                            }

                            if (showGoalDialog) {
                                SetSavingsGoalDialog(
                                    currentGoal = monthlyGoal,
                                    onDismiss = { showGoalDialog = false },
                                    onConfirm = { processor.updateMonthlyGoal(it); showGoalDialog = false }
                                )
                            }
                        }
                    }
                } else {
                    AuthLockedScreen {
                        BiometricAuth.authenticate(this@MainActivity) { isAuthenticated = it }
                    }
                }
            }
        }
    }
}

// --- Specialized UI Components ---

@Composable
fun OnboardingNameDialog(onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Dialog(onDismissRequest = {}) {
        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color(0xFF00796B))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Welcome!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("What should we call you?", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { if (text.isNotBlank()) onConfirm(text) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                ) { Text("Continue") }
            }
        }
    }
}

@Composable
fun OnboardingCurrencyDialog(onConfirm: (String) -> Unit) {
    val currencies = listOf("USD", "EUR", "GBP", "INR", "JPY")
    Dialog(onDismissRequest = {}) {
        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Setup Currency", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Pick the primary currency for your wallet.", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                currencies.forEach { currency ->
                    OutlinedButton(
                        onClick = { onConfirm(currency) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text(currency) }
                }
            }
        }
    }
}

@Composable
fun AuthLockedScreen(onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Lock, contentDescription = "Locked", modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(24.dp))
            Text("Companion Locked", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onRetry, shape = RoundedCornerShape(12.dp)) { Text("Authenticate") }
        }
    }
}

@Composable
fun SetSavingsGoalDialog(currentGoal: Double, onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var goalInput by remember { mutableStateOf(currentGoal.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Monthly Savings Goal", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = goalInput,
                onValueChange = { goalInput = it },
                label = { Text("Goal Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            Button(onClick = {
                val value = goalInput.toDoubleOrNull() ?: 0.0
                if (value > 0) onConfirm(value)
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun TransferDialog(onConfirm: (Double) -> Unit, onDismiss: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Move to Savings", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        },
        confirmButton = {
            Button(onClick = {
                val value = amount.toDoubleOrNull() ?: 0.0
                if (value > 0) onConfirm(value)
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))) { Text("Confirm") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransaction(onSave: (Transaction) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    val categories = listOf("Food", "Entertainment", "Shopping", "Transport", "Health", "Other")
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    Column(Modifier.padding(24.dp).fillMaxWidth().navigationBarsPadding()) {
        Text("New Entry", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
            Switch(checked = isIncome, onCheckedChange = { isIncome = it })
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isIncome) "Income" else "Expense")
        }
        if (!isIncome) {
            Text("Category", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                items(categories) { category ->
                    FilterChip(selected = selectedCategory == category, onClick = { selectedCategory = category }, label = { Text(category) })
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            onSave(Transaction(id = 0, title = title, amount = amount.toDoubleOrNull() ?: 0.0, category = if (isIncome) "Salary" else selectedCategory, isIncome = isIncome, subtitle = if (isIncome) "Credit" else "Debit", date = "Today"))
            onDismiss()
        }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), enabled = title.isNotBlank() && amount.isNotBlank()) {
            Text("Save Entry")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceCompanionTopBar(onProfileClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text("Finance Companion",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.size(20.dp))
            }
        },
        actions = {
            IconButton(onClick = { /* Notificaton Logic */ }) {
                Icon(Icons.Default.Notifications, contentDescription = null)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
fun FinanceCompanionBottomNavigation(activeTab: Screen, onTabSelected: (Screen) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))) {
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
                colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF00796B), selectedTextColor = Color(0xFF00796B), indicatorColor = Color.Transparent)
            )
        }
    }
}