package com.example.financecompanion.HomeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Financial Insights",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0A2540)
            )
            Text("Understand your spending patterns", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Visual Element: Spending Progress or Comparison
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Weekly Spending", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    // A simple Bar Chart placeholder using colored Boxes
                    Row(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Bar(0.4f, "Mon")
                        Bar(0.7f, "Tue")
                        Bar(0.3f, "Wed")
                        Bar(0.9f, "Thu") // Peak spending
                        Bar(0.5f, "Fri")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Category Breakdown List
        item { Text("TOP CATEGORIES", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black) }

        val categories = state.recentEntries.filter { !it.isIncome }.groupBy { it.category }
        items(categories.toList()) { (category, list) ->
            val total = list.sumOf { it.amount }
            CategoryInsightRow(category, total, state.totalExpenses)
        }
    }
}

@Composable
fun Bar(fraction: Float, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(20.dp)
                .fillMaxHeight(fraction)
                .background(Color(0xFF00796B), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
        )
        Text(label, fontSize = 10.sp, color = Color.Gray)
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