package com.example.financecompanion.HomeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financecompanion.dataModel.model.VaultState

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

        // 1. Spending Chart (Visual Representation)
        item { SpendingChartCard() }

        // 2. Weekly Trend (Requirement: Trend Indicator)
        item { WeeklyTrendCard() }

        // 3. Progress Indicator (Requirement: Savings Goal)
        item {
            SavingsGoalCard(current = 1200.0, target = 2000.0)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 4. Category Breakdown (Requirement: Spending by Category)
        item { Text("CATEGORY BREAKDOWN", fontWeight = FontWeight.Bold, color = Color.Gray) }

        val categories = state.recentEntries.filter { !it.isIncome }.groupBy { it.category }
        items(categories.toList()) { (category, list) ->
            CategoryInsightRow(
                category = category,
                categoryAmount = list.sumOf { it.amount },
                totalExpenses = state.totalExpenses
            )
        }
    }
}
@Composable
fun CategoryInsightRow(
    category: String,
    categoryAmount: Double,
    totalExpenses: Double
) {
    // Calculate percentage of total spend
    val percentage = if (totalExpenses > 0) (categoryAmount / totalExpenses).toFloat() else 0f
    val displayPercentage = (percentage * 100).toInt()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 0.5.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Category Name and Amount
                Column {
                    Text(
                        text = category,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1A202C)
                    )
                    Text(
                        text = "$${String.format("%.2f", categoryAmount)} total",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                // 2. Percentage Text
                Text(
                    text = "$displayPercentage%",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = Color(0xFF00796B)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Visual Progress Bar (Requirement 4: Visual Element)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9)) // Track color
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = percentage.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(
                            // Change color to Red if a category takes up > 50% of budget
                            if (percentage > 0.5f) Color(0xFFD32F2F) else Color(0xFF00796B),
                            CircleShape
                        )
                )
            }
        }
    }
}
@Composable
fun SpendingChartCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Spending Activity", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(20.dp))

            // Simplified Line Chart Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShowChart,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFF00796B).copy(alpha = 0.2f)
                )
                Text("Daily Expense Curve", color = Color.LightGray, fontSize = 12.sp)
            }
        }
    }
}
@Composable
fun WeeklyTrendCard() {
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
                Text("Weekly Trend", color = Color.Gray, fontSize = 13.sp)
                Text("+$120.50", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color(0xFFD32F2F))
                Text("More than last week", fontSize = 12.sp, color = Color.Gray)
            }

            // Trend Icon
            Box(
                modifier = Modifier.size(50.dp).background(Color(0xFFFFEBEE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F)
                )
            }
        }
    }
}