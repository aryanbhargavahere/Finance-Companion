package com.example.financecompanion.HomeScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financecompanion.dataModel.model.VaultState
import java.util.Locale

@Composable
fun InsightsScreen(state: VaultState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                "Financial Analysis",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        }

        // 1. Visual Pie Chart Card
        item { SpendingChartCard(state) }

        // 2. Net Balance / Trend Card
        item { WeeklyTrendCard(state) }

        // 3. Goal Progress (Tracks "Add to Savings" transactions)
        item {
            val actualSavings = state.recentEntries
                .filter {
                    it.category == "Savings Vault" ||
                            it.title.contains("Add to Savings", ignoreCase = true)
                }
                .sumOf { it.amount }
                .toDouble() // Fix for overload resolution ambiguity

            SavingsGoalCard(current = actualSavings, target = 2000.0)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 4. Category Breakdown
        val categories = state.recentEntries
            .filter { !it.isIncome && it.category != "Savings Vault" && !it.title.contains("Add to Savings", ignoreCase = true) }
            .groupBy { it.category }

        if (categories.isNotEmpty()) {
            item { Text("CATEGORY BREAKDOWN", fontWeight = FontWeight.Bold, color = Color.Gray) }
            items(categories.toList()) { (category, list) ->
                CategoryInsightRow(
                    category = category,
                    categoryAmount = list.sumOf { it.amount },
                    totalExpenses = state.totalExpenses
                )
            }
        }
    }
}

@Composable
fun SpendingChartCard(state: VaultState) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(100.dp)) {
                    drawArc(
                        color = Color(0xFFF1F5F9),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 20f)
                    )
                    val sweep = if (state.totalIncome > 0) (state.totalExpenses / state.totalIncome).toFloat() * 360f else 0f
                    drawArc(
                        color = Color(0xFF00796B),
                        startAngle = -90f,
                        sweepAngle = sweep.coerceIn(0f, 360f),
                        useCenter = false,
                        style = Stroke(width = 20f, cap = StrokeCap.Round)
                    )
                }
                Text("${((state.totalExpenses / state.totalIncome.coerceAtLeast(1.0)) * 100).toInt()}%", fontWeight = FontWeight.Black, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(24.dp))

            Column {
                Text("Expense Ratio", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("vs. Total Income", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("$${String.format(Locale.US, "%.2f", state.totalExpenses)}", fontWeight = FontWeight.Black, color = Color(0xFF00796B))
            }
        }
    }
}

@Composable
fun WeeklyTrendCard(state: VaultState) {
    val balance = state.totalIncome - state.totalExpenses
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Net Balance", color = Color.Gray, fontSize = 13.sp)
                Text(
                    text = if(balance >= 0) "+$${String.format(Locale.US, "%.2f", balance)}" else "-$${String.format(Locale.US, "%.2f", kotlin.math.abs(balance))}",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = if (balance >= 0) Color(0xFF00796B) else Color(0xFFD32F2F)
                )
                Text(if (balance >= 0) "Safe to spend" else "Over budget", fontSize = 12.sp, color = Color.Gray)
            }

            Box(
                modifier = Modifier.size(50.dp).background(if(balance < 0) Color(0xFFFFEBEE) else Color(0xFFE0F2F1), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if(balance >= 0) Icons.Default.TrendingUp else Icons.Default.ShowChart,
                    contentDescription = null,
                    tint = if(balance < 0) Color(0xFFD32F2F) else Color(0xFF00796B)
                )
            }
        }
    }
}

@Composable
fun CategoryInsightRow(category: String, categoryAmount: Double, totalExpenses: Double) {
    val percentage = if (totalExpenses > 0) (categoryAmount / totalExpenses).toFloat() else 0f
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 0.5.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(category, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("$${String.format(Locale.US, "%.0f", categoryAmount)}", fontWeight = FontWeight.Black, color = Color(0xFF00796B))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFF1F5F9), CircleShape)) {
                Box(Modifier.fillMaxWidth(percentage.coerceIn(0f, 1f)).fillMaxHeight().background(Color(0xFF00796B), CircleShape))
            }
        }
    }
}