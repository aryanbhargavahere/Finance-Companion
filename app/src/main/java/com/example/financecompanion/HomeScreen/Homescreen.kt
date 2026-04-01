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
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.financecompanion.dataModel.model.Dataledger
import com.example.financecompanion.dataModel.model.Transaction
import com.example.financecompanion.dataModel.model.VaultState

@Composable
fun HomeDashboard(state: VaultState,
                  onAddTransactionClicked: () -> Unit) {
    LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
        item {
            // Summary Card (Balance, Income, Expenses)
            WealthCard(state.balance, state.totalIncome, state.totalExpenses)
        }

        // Requirement 3: Goal/Challenge Feature
        item {
            SavingsGoalCard(current = 1200.0, target = 2000.0)
        }

        item { QuickActionRow(onAddClick = onAddTransactionClicked) }

        item { Text("RECENT ACTIVITY", fontWeight = FontWeight.Black) }
        items(state.recentEntries.take(5)) { tx ->
            TransactionRow(tx)
        }
    }
}

@Composable
fun SavingsGoalCard(current: Double, target: Double) {
    val progress = (current / target).toFloat()
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp)) {
            Text("Monthly Savings Goal", fontWeight = FontWeight.Bold)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clip(CircleShape),
                color = Color(0xFF00796B)
            )
            Text("${(progress * 100).toInt()}% of $target reached", fontSize = 12.sp, color = Color.Gray)
        }
    }
}
@Composable
fun WealthCard(
    balance: Double,
    income: Double,
    expenses: Double
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
                text = "$${String.format("%.2f", balance)}",
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
                FinanceSummaryItem("INCOME", income, Color(0xFF4ADE80))
                FinanceSummaryItem("EXPENSES", expenses, Color(0xFF22D3EE))
            }
        }
    }
}

@Composable
fun FinanceSummaryItem(label: String, amount: Double, color: Color) {
    Column {
        Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Text(
            text = "${if (label == "INCOME") "+" else ""}$${String.format("%.2f", amount)}",
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}
@Composable
fun QuickActionRow(onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ActionItem("Add", Icons.Default.Add, Color(0xFFB2EBF2), onAddClick)
        ActionItem("Transfer", Icons.Default.SyncAlt, Color(0xFFE3F2FD), {})
        ActionItem("Insights", Icons.Default.PieChart, Color(0xFFC8E6C9), {})
    }
}

@Composable
fun ActionItem(label: String, icon: ImageVector, bgColor: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp)
                .background(bgColor, RoundedCornerShape(16.dp))
        ) {
            Icon(icon, contentDescription = label, tint = Color(0xFF0A2540))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
@Composable
fun TransactionRow(
    transaction: Transaction,
    onDelete: (String) -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF1F5F9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.isIncome) Icons.Default.TrendingUp else Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF4A5568)
                )
            }

            Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                Text(transaction.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(transaction.category, color = Color.Gray, fontSize = 12.sp)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (transaction.isIncome) "+" else "-"}$${String.format("%.2f", transaction.amount)}",
                    fontWeight = FontWeight.ExtraBold,
                    color = if (transaction.isIncome) Color(0xFF2E7D32) else Color(0xFF1A202C)
                )
                Text(transaction.date, color = Color.LightGray, fontSize = 10.sp)
            }
        }
    }
}