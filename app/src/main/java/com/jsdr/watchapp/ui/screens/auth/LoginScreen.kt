package com.jsdr.watchapp.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jsdr.watchapp.BrandPurple
import com.jsdr.watchapp.ui.navigation.Screen

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {

        if (uiState.isSuccess) {

            navController.navigate(Screen.Profile.route) {

                popUpTo(Screen.Login.route) {
                    inclusive = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Logowanie",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = {
                viewModel.updateEmail(it)
            },
            label = {
                Text(
                    text = "Email",
                    color = Color.White
                )
            },
            modifier = Modifier.fillMaxWidth(),

            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedBorderColor = BrandPurple,
                unfocusedBorderColor = Color.Gray,
                cursorColor = BrandPurple
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = {
                viewModel.updatePassword(it)
            },
            label = {
                Text(
                    text = "Hasło",
                    color = Color.White
                )
            },
            modifier = Modifier.fillMaxWidth(),

            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedBorderColor = BrandPurple,
                unfocusedBorderColor = Color.Gray,
                cursorColor = BrandPurple
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.login()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPurple
            )
        ) {
            Text("Zaloguj")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                navController.navigate(Screen.Register.route)
            }
        ) {
            Text(
                text = "Nie masz konta? Zarejestruj się",
                color = Color.White
            )
        }
        uiState.emailError?.let {

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = it,
                color = Color.Red
            )
        }
    }
}