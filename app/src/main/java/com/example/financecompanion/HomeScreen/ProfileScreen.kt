package com.example.financecompanion.HomeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financecompanion.dataModel.model.VaultState


@Composable
fun ProfileScreen(
    state: VaultState,
    onNavigateToAppearance: () -> Unit,
    onNavigateToCurrency: () -> Unit,
    onNavigateToPersonal: () -> Unit,
    userName: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { Spacer(modifier = Modifier.height(32.dp)) }

        // 1. HEADER: USER AVATAR & NAME
        item {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0A2540)),
                contentAlignment = Alignment.Center
            ) {
                // In a real app, use an Image() with a painter here
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Aryan", // Using your identified name
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0A2540)
            )
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        // 2. SETTINGS CATEGORIES
        item {
            ProfileSectionHeader("ACCOUNT SETTINGS")
            ProfileOptionItem("Personal Information", Icons.Default.Badge, onClick = {onNavigateToPersonal()})
            ProfileOptionItem("Security & Privacy", Icons.Default.Shield, onClick = {})
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            ProfileSectionHeader("PREFERENCES")
            ProfileOptionItem("Notifications", Icons.Default.NotificationsNone, onClick = {})
            ProfileOptionItem("Currency", Icons.Default.AttachMoney, onClick = {onNavigateToCurrency()})
            ProfileOptionItem("Appearance", Icons.Default.Palette, onClick = {onNavigateToAppearance()})
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        // 3. LOGOUT BUTTON
        item {
            Button(
                onClick = { /* Handle Logout */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Log Out", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(100.dp)) // Bottom Nav Clearance
        }
    }
}

@Composable
fun ProfileSectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        style = androidx.compose.ui.text.TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        ),
        color = Color.Gray
    )
}

@Composable
fun ProfileOptionItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        color = Color.White,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF4A5568), modifier = Modifier.size(20.dp))
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 16.dp).weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}