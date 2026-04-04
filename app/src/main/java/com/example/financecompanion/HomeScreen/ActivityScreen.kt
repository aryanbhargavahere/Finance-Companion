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
import com.example.financecompanion.HomeScreen.TransactionRow
import com.example.financecompanion.dataModel.model.VaultState

@Composable
fun ActivityScreen(state: VaultState) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1. SEARCH BAR
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
            // Filtering logic based on the 'date' field and search query
            val todayEntries = state.recentEntries.filter {
                it.date.contains("Today", ignoreCase = true) &&
                        it.title.contains(searchQuery, ignoreCase = true)
            }

            val yesterdayEntries = state.recentEntries.filter {
                it.date.contains("Yesterday", ignoreCase = true) &&
                        it.title.contains(searchQuery, ignoreCase = true)
            }

            val otherEntries = state.recentEntries.filter {
                !it.date.contains("Today", ignoreCase = true) &&
                        !it.date.contains("Yesterday", ignoreCase = true) &&
                        it.title.contains(searchQuery, ignoreCase = true)
            }

            // Section: TODAY
            if (todayEntries.isNotEmpty()) {
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

                items(todayEntries) { entry ->
                    TransactionRow(entry)
                }
            }

            // Section: YESTERDAY
            if (yesterdayEntries.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
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

                items(yesterdayEntries) { entry ->
                    TransactionRow(entry)
                }
            }

            // Section: PREVIOUS (For everything else)
            if (otherEntries.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "PREVIOUS",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(otherEntries) { entry ->
                    TransactionRow(entry)
                }
            }

            // Final check: if the total list is empty
            if (state.recentEntries.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("No transactions found", color = Color.Gray)
                    }
                }
            }

            // Padding at the bottom for the Navigation Bar
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}