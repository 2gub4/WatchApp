package com.jsdr.watchapp.ui.components
import com.jsdr.watchapp.ui.navigation.Screen
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jsdr.watchapp.BrandPurple

@Composable
fun WatchAppBottomBar(navController: NavController, currentRoute: String?) {
    val items = listOf(Screen.Home, Screen.Search, Screen.Lists, Screen.Profile)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(BrandPurple)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val isSelected = currentRoute == screen.route
                BottomNavIconPlaceholder(
                    icon = screen.icon!!,
                    isSelected = isSelected,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavIconPlaceholder(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(
                width = 2.dp,
                color = if (isSelected) Color.White else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            )
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(26.dp)
        )
    }
}