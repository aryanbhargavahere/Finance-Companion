package com.example.financecompanion.HomeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financecompanion.HomeScreen.TransactionRow // Reusing the row component we made earlier
import com.example.financecompanion.dataModel.model.VaultState

@Composable
fun ActivityScreen(state: VaultState) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // Clean slate background
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1. SEARCH BAR (Matching the top of your 2nd image)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = { Text("Search transactions...", color = Color.Gray) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFF00796B)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. TRANSACTION LIST WITH DATE GROUPING
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Section: TODAY
            item {
                Text(
                    text = "TODAY",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Filtering logic based on date and search query
            val todayEntries = state.recentEntries.filter {
                it.subtitle.contains("Today", ignoreCase = true) &&
                        it.title.contains(searchQuery, ignoreCase = true)
            }

            items(todayEntries) { entry ->
                TransactionRow(entry)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Section: YESTERDAY
            item {
                Text(
                    text = "YESTERDAY",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            val yesterdayEntries = state.recentEntries.filter {
                it.subtitle.contains("Yesterday", ignoreCase = true) &&
                        it.title.contains(searchQuery, ignoreCase = true)
            }

            items(yesterdayEntries) { entry ->
                TransactionRow(entry)
            }

            // Padding at the bottom for the Navigation Bar
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}