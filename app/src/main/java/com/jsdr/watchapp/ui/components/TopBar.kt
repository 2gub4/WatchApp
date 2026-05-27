package com.jsdr.watchapp.ui.components

import androidx.compose.foundation.clickable
import com.jsdr.watchapp.ui.navigation.Screen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jsdr.watchapp.BrandPurple


@Composable
fun WatchAppTopBar(navController: NavController, currentRoute: String?) {
    val isSettingsActive = currentRoute == Screen.Settings.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = BrandPurple)) {
                    append("Watch")
                }

                withStyle(style = SpanStyle(color = Color.White)) {
                    append("!")
                }

                withStyle(style = SpanStyle(color = BrandPurple)) {
                    append("App")
                }
            },

            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,

            modifier = Modifier.clickable {

                navController.navigate(Screen.Home.route) {

                    popUpTo(Screen.Home.route) {
                        inclusive = false
                    }

                    launchSingleTop = true
                }
            }
        )

        IconButton(onClick = {
            if (!isSettingsActive) {
                navController.navigate(Screen.Settings.route) {
                    launchSingleTop = true
                }
            }
        }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Ustawienia",
                tint = if (isSettingsActive) Color.White else BrandPurple,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}