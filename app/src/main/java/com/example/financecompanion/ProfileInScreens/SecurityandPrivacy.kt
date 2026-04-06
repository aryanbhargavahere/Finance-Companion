package com.example.financecompanion.ProfileInScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceCompanionSecurityPrivacyScreen(
    hideBalance: Boolean,
    onHideBalanceChange: (Boolean) -> Unit,
    lockTimer: Int,
    onLockTimerChange: (Int) -> Unit,
    onResetData: () -> Unit,
    onBack: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Security & Privacy", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            item {
                Text(
                    text = "PROTECTION",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // Hide Balance Toggle (When Activated The Balance Will Be Converted To Asterisks)
            item {
                SecurityToggleItem(
                    title = "Hide Balances",
                    subtitle = "Mask amounts on the home screen",
                    icon = Icons.Default.VisibilityOff,
                    checked = hideBalance,
                    onCheckedChange = onHideBalanceChange
                )
            }

            // Auto Lock Timer
            item {
                SecurityClickableItem(
                    title = "Auto-Lock Timer",
                    subtitle = "Lock app after ${if (lockTimer == 0) "Immediate" else "$lockTimer seconds"}",
                    icon = Icons.Default.LockClock,
                    onClick = {
                        val next = when (lockTimer) {
                            0 -> 30
                            30 -> 60
                            else -> 0
                        }
                        onLockTimerChange(next)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "RESET DATA",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }

            // Reset the data (IF allowed all the user data will be removed)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = Color.Red.SimpleAlpha(0.05f),
                    shape = RoundedCornerShape(16.dp),
                    onClick = { showResetDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = Color.Red
                        )
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(
                                text = "Reset Data",
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                            Text(
                                text = "Permanently delete all transactions",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Clear All Data?") },
                text = { Text("This action cannot be undone. All your records will be deleted.") },
                confirmButton = {
                    TextButton(onClick = {
                        onResetData()
                        showResetDialog = false
                    }) {
                        Text("Confirm Reset", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

private fun Color.SimpleAlpha(alpha: Float): Color = this.copy(alpha = alpha)

@Composable
fun SecurityToggleItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SecurityClickableItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}