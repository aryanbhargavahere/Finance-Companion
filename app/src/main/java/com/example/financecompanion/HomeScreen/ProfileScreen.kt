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
    onNavigateToNotifications: () -> Unit,
    userName: String,
    onLogoutClick: () -> Unit,
    onNavigateToSecurity: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            // UPDATED: Used background from theme instead of hardcoded F8FAFC
            .background(MaterialTheme.colorScheme.background)
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
                    // UPDATED: SurfaceVariant gives a nice subtle grey in dark mode
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    // UPDATED: Tint now reacts to the surface color
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = userName, // Dynamically use the passed userName
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                // UPDATED: Text color now switches between Black and White automatically
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        // 2. SETTINGS CATEGORIES
        item {
            ProfileSectionHeader("ACCOUNT SETTINGS")
            ProfileOptionItem("Personal Information", Icons.Default.Badge, onClick = { onNavigateToPersonal() })
            ProfileOptionItem("Security & Privacy", Icons.Default.Shield, onClick = {onNavigateToSecurity()})
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            ProfileSectionHeader("PREFERENCES")
            ProfileOptionItem("Notifications", Icons.Default.NotificationsNone, onClick = { onNavigateToNotifications() })
            ProfileOptionItem("Currency", Icons.Default.AttachMoney, onClick = { onNavigateToCurrency() })
            ProfileOptionItem("Appearance", Icons.Default.Palette, onClick = { onNavigateToAppearance() })
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        // 3. LOGOUT BUTTON
        item {
            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                // UPDATED: Using error container colors for a "Danger" look that works in dark mode
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Log Out", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(100.dp))
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
        // UPDATED: onSurface with alpha ensures it stays readable on dark backgrounds
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
}

@Composable
fun ProfileOptionItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        // UPDATED: Used 'surface' instead of hardcoded Color.White
        color = MaterialTheme.colorScheme.surface,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp // Slightly increased for better visibility in dark mode
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // UPDATED: Icons now use primary or onSurface color
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 16.dp).weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}