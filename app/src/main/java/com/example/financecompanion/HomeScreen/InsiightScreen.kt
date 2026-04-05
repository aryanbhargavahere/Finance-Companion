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
fun InsightsScreen(
    state: VaultState,
    currencySymbol: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                "Financial Analysis",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        }

        // 1. Visual Pie Chart Card
        item { SpendingChartCard(state, currencySymbol) }

        // 2. Net Balance / Trend Card
        item { WeeklyTrendCard(state, currencySymbol) }

        // 3. Category Breakdown
        // Filter out income and savings to focus strictly on expenses
        val categories = state.recentEntries
            .filter { !it.isIncome && it.category != "Savings" }
            .groupBy { it.category }

        if (categories.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "CATEGORY BREAKDOWN",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(categories.toList()) { (category, list) ->
                CategoryInsightRow(
                    category = category,
                    categoryAmount = list.sumOf { it.amount },
                    totalExpenses = state.totalExpenses,
                    currencySymbol = currencySymbol
                )
            }
        } else {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No expense data to analyze", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun SpendingChartCard(state: VaultState, currencySymbol: String) {
    val expenseColor = Color(0xFF00796B)
    val onSurface = MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        color = Color.LightGray.copy(alpha = 0.2f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 20f)
                    )
                    val sweep = if (state.totalIncome > 0)
                        (state.totalExpenses / state.totalIncome).toFloat() * 360f
                    else 0f

                    drawArc(
                        color = expenseColor,
                        startAngle = -90f,
                        sweepAngle = sweep.coerceIn(0f, 360f),
                        useCenter = false,
                        style = Stroke(width = 20f, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "${((state.totalExpenses / state.totalIncome.coerceAtLeast(1.0)) * 100).toInt()}%",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = onSurface
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            Column {
                Text("Expense Ratio", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = onSurface)
                Text("vs. Total Income", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "$currencySymbol${String.format(Locale.US, "%.2f", state.totalExpenses)}",
                    fontWeight = FontWeight.Black,
                    color = expenseColor
                )
            }
        }
    }
}

@Composable
fun WeeklyTrendCard(state: VaultState, currencySymbol: String) {
    val balance = state.totalIncome - (state.totalExpenses + state.totalSavings)
    val positiveColor = Color(0xFF00796B)
    val negativeColor = Color(0xFFD32F2F)

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Net Balance", color = Color.Gray, fontSize = 13.sp)
                Text(
                    text = if(balance >= 0) "+$currencySymbol${String.format(Locale.US, "%.2f", balance)}"
                    else "-$currencySymbol${String.format(Locale.US, "%.2f", kotlin.math.abs(balance))}",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = if (balance >= 0) positiveColor else negativeColor
                )
                Text(
                    text = if (balance >= 0) "Safe to spend" else "Over budget",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        if(balance < 0) negativeColor.copy(alpha = 0.1f)
                        else positiveColor.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if(balance >= 0) Icons.Default.TrendingUp else Icons.Default.ShowChart,
                    contentDescription = null,
                    tint = if(balance < 0) negativeColor else positiveColor
                )
            }
        }
    }
}

@Composable
fun CategoryInsightRow(category: String, categoryAmount: Double, totalExpenses: Double, currencySymbol: String) {
    val percentage = if (totalExpenses > 0) (categoryAmount / totalExpenses).toFloat() else 0f

    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 0.5.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(category, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    "$currencySymbol${String.format(Locale.US, "%.0f", categoryAmount)}",
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00796B)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(Color.LightGray.copy(alpha = 0.2f))) {
                Box(
                    Modifier
                        .fillMaxWidth(percentage.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(Color(0xFF00796B), CircleShape)
                )
            }
        }
    }
}