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

        // 1. Visual Pie Chart Card (Breakdown by Category)
        item { SpendingPieChartCard(state) }

        // 2. Expense Ratio Card
        item { SpendingChartCard(state, currencySymbol) }

        // 3. Net Balance / Trend Card
        item { WeeklyTrendCard(state, currencySymbol) }

        // 4. Category Breakdown List
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
fun SpendingPieChartCard(state: VaultState) {
    // Filter and group data
    val categoryTotals = state.recentEntries
        .filter { !it.isIncome && it.category != "Savings" }
        .groupBy { it.category }
        .mapValues { it.value.sumOf { entry -> entry.amount } }

    val totalSpending = categoryTotals.values.sum()

    val chartColors = listOf(
        Color(0xFF4F1575), Color(0xFFC4052B), Color(0xFF1A37A9),
        Color(0xFFCCB80D), Color(0xFF3AC0B4), Color(0xFFE8F1F0)
    )

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Spending Distribution",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (totalSpending > 0) {
                Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        var startAngle = -90f
                        categoryTotals.values.forEachIndexed { index, amount ->
                            val sweepAngle = (amount.toFloat() / totalSpending.toFloat()) * 360f
                            drawArc(
                                color = chartColors[index % chartColors.size],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 75f, cap = StrokeCap.Butt)
                            )
                            startAngle += sweepAngle
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            String.format(Locale.US, "%.0f", totalSpending),
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    categoryTotals.keys.take(3).forEachIndexed { index, category ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                            Box(modifier = Modifier.size(8.dp).background(chartColors[index % chartColors.size], CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(category, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                Text("Not enough data", color = Color.Gray, modifier = Modifier.padding(20.dp))
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