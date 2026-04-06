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
import com.example.financecompanion.dataModel.model.ViewModelState

@Composable
fun FinanceCompanionProfileScreen(
    state: ViewModelState,
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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { Spacer(modifier = Modifier.height(32.dp)) }

        // Show users avatar(person icon for now) and users name
        item {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = userName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        // Different setting items
        item {
            ProfileSectionHeader("ACCOUNT SETTINGS")
            ProfileScreenOptions("Personal Information", Icons.Default.Badge, onClick = { onNavigateToPersonal() })
            ProfileScreenOptions("Security & Privacy", Icons.Default.Shield, onClick = {onNavigateToSecurity()})
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            ProfileSectionHeader("PREFERENCES")
            ProfileScreenOptions("Notifications", Icons.Default.NotificationsNone, onClick = { onNavigateToNotifications() })
            ProfileScreenOptions("Currency", Icons.Default.AttachMoney, onClick = { onNavigateToCurrency() })
            ProfileScreenOptions("Appearance", Icons.Default.Palette, onClick = { onNavigateToAppearance() })
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        // Logout Button to go out of the appp
        item {
            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
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
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
}

@Composable
fun ProfileScreenOptions(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        color = MaterialTheme.colorScheme.surface,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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