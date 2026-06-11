package com.jsdr.watchapp.ui.screens.auth

import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jsdr.watchapp.BrandPurple
import com.jsdr.watchapp.ui.navigation.Screen

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var repeatPasswordVisible by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.isSuccess) {

        if (uiState.isSuccess) {

            navController.navigate(Screen.Profile.route) {

                popUpTo(Screen.Register.route) {
                    inclusive = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Rejestracja",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.username,
            onValueChange = {
                viewModel.updateUsername(it)
            },
            label = {
                Text(
                    text = "Nazwa użytkownika",
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
        uiState.usernameError?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

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
        uiState.emailError?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.birthYear,
            onValueChange = {
                viewModel.updateBirthYear(it)
            },
            label = {
                Text(
                    text = "Rok urodzenia",
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
        uiState.birthYearError?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Płeć",
            color = Color.White
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            FilterChip(
                selected = uiState.gender == "Mężczyzna",
                onClick = {
                    viewModel.updateGender("Mężczyzna")
                },
                label = {
                    Text(
                        text = "Mężczyzna",
                        color = Color.White
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandPurple,
                    selectedLabelColor = Color.White
                )
            )

            FilterChip(
                selected = uiState.gender == "Kobieta",
                onClick = {
                    viewModel.updateGender("Kobieta")
                },
                label = {
                    Text(
                        text = "Kobieta",
                        color = Color.White
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandPurple,
                    selectedLabelColor = Color.White
                )
            )
        }

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
            visualTransformation =
                if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),

            trailingIcon = {
                IconButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {
                    Icon(
                        imageVector =
                            if (passwordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = BrandPurple
                    )
                }
            },

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
            value = uiState.repeatPassword,
            onValueChange = {
                viewModel.updateRepeatPassword(it)
            },
            label = {
                Text(
                    text = "Powtórz hasło",
                    color = Color.White
                )
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation =
                if (repeatPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),

            trailingIcon = {
                IconButton(
                    onClick = {
                        repeatPasswordVisible = !repeatPasswordVisible
                    }
                ) {
                    Icon(
                        imageVector =
                            if (repeatPasswordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = BrandPurple
                    )
                }
            },

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
        uiState.passwordError?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.register()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPurple
            )
        ) {
            Text("Zarejestruj")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                navController.navigate(Screen.Login.route)
            }
        ){
            Text("Masz już konto? Zaloguj się")
        }
        }
    }
