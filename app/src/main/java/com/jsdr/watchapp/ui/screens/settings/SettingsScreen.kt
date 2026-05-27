package com.jsdr.watchapp.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jsdr.watchapp.BrandPurple
import com.jsdr.watchapp.DarkBackground

@Composable
fun SettingsScreen(
    navController: NavController
) {

    var language by remember {
        mutableStateOf("Polski")
    }

    var showLanguageDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(20.dp)
    ) {

        Text(
            text = "Ustawienia",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(30.dp))

        // JĘZYK

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BrandPurple)
                .clickable {
                    showLanguageDialog = true
                }
                .padding(20.dp)
        ) {

            Text(
                text = "Język",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = language,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // DIALOG

    if (showLanguageDialog) {

        AlertDialog(
            onDismissRequest = {
                showLanguageDialog = false
            },

            containerColor = DarkBackground,

            title = {
                Text(
                    text = "Wybierz język",
                    color = Color.White
                )
            },

            text = {

                Column {

                    Text(
                        text = "Polski",
                        color = Color.White,
                        fontSize = 20.sp,

                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                language = "Polski"
                                showLanguageDialog = false
                            }
                            .padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "English",
                        color = Color.White,
                        fontSize = 20.sp,

                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                language = "English"
                                showLanguageDialog = false
                            }
                            .padding(16.dp)
                    )
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showLanguageDialog = false
                    }
                ) {

                    Text(
                        text = "OK",
                        color = BrandPurple
                    )
                }
            }
        )
    }
}