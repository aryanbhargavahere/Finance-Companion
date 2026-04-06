package com.example.financecompanion.HomeScreen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.financecompanion.dataModel.model.Transaction
import com.example.financecompanion.dataModel.model.VaultState
import java.util.Locale

@Composable
fun HomeDashboard(
    state: VaultState,
    monthlyGoal: Double,
    currencySymbol: String = "$",
    hideBalance: Boolean = false, // NEW: Parameter to control visibility
    onAddTransactionClicked: () -> Unit,
    onTransferClicked: () -> Unit,
    onInsightsClicked: () -> Unit,
    onEditGoalClicked: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 20.dp, start = 20.dp, end = 20.dp)
    ) {
        item {
            WealthCard(
                balance = state.balance,
                income = state.totalIncome,
                expenses = state.totalExpenses,
                currencySymbol = currencySymbol,
                hideBalance = hideBalance // Pass state down
            )
        }

        item {
            Surface(
                onClick = onEditGoalClicked,
                color = Color.Transparent,
                shape = RoundedCornerShape(24.dp)
            ) {
                SavingsGoalCard(
                    current = state.totalSavings,
                    target = monthlyGoal,
                    currencySymbol = currencySymbol
                )
            }
        }

        item {
            QuickActionRow(
                onAddClick = onAddTransactionClicked,
                onTransferClick = onTransferClicked,
                onInsightsClick = onInsightsClicked
            )
        }

        item {
            Text(
                "RECENT ACTIVITY",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.Gray
            )
        }

        val recentItems = state.recentEntries.take(5)
        if (recentItems.isEmpty()) {
            item {
                Text(
                    "No recent transactions",
                    modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Color.Gray
                )
            }
        } else {
            items(recentItems) { tx ->
                TransactionRow(
                    transaction = tx,
                    currencySymbol = currencySymbol
                )
            }
        }
    }
}

@Composable
fun QuickActionRow(
    onAddClick: () -> Unit,
    onTransferClick: () -> Unit,
    onInsightsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ActionItem("Add", Icons.Default.Add, Color(0xFFB2EBF2), onAddClick)
        ActionItem("To Savings", Icons.Default.SyncAlt, Color(0xFFE3F2FD), onTransferClick)
        ActionItem("Insights", Icons.Default.PieChart, Color(0xFFC8E6C9), onInsightsClick)
    }
}

@Composable
fun SavingsGoalCard(
    current: Double,
    target: Double,
    currencySymbol: String = "$"
) {
    val progress = (current / target.coerceAtLeast(1.0)).toFloat().coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Monthly Saving Goal",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                )
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "$currencySymbol${String.format(Locale.US, "%,.0f", current)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    " / $currencySymbol${String.format(Locale.US, "%,.0f", target)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "${(progress * 100).toInt()}% of your goal reached",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun WealthCard(
    balance: Double,
    income: Double,
    expenses: Double,
    currencySymbol: String = "$",
    hideBalance: Boolean = false // NEW: Parameter
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A2540))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "TOTAL BALANCE",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )

            Text(
                text = if (hideBalance) "****" else "$currencySymbol${String.format(Locale.US, "%,.2f", balance)}",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FinanceSummaryItem("INCOME", income, Color(0xFF4ADE80), currencySymbol, hideBalance)
                FinanceSummaryItem("EXPENSES", expenses, Color(0xFF22D3EE), currencySymbol, hideBalance)
            }
        }
    }
}

@Composable
fun FinanceSummaryItem(
    label: String,
    amount: Double,
    color: Color,
    currencySymbol: String,
    hideBalance: Boolean = false // NEW: Parameter
) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (hideBalance) "****" else "${if (label == "INCOME") "+" else ""}$currencySymbol${String.format(Locale.US, "%,.2f", amount)}",
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun ActionItem(label: String, icon: ImageVector, bgColor: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp).background(bgColor, RoundedCornerShape(16.dp))
        ) {
            Icon(icon, contentDescription = label, tint = Color(0xFF0A2540))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun TransactionRow(
    transaction: Transaction,
    currencySymbol: String = "$"
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.isIncome) Icons.Default.TrendingUp else Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = transaction.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = transaction.category,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                val amountColor = if (transaction.isIncome) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                Text(
                    text = "${if (transaction.isIncome) "+" else "-"}$currencySymbol${String.format(Locale.US, "%,.2f", transaction.amount)}",
                    fontWeight = FontWeight.ExtraBold,
                    color = amountColor
                )

                Text(
                    text = transaction.date,
                    color = Color.LightGray,
                    fontSize = 10.sp
                )
            }
        }
    }
}