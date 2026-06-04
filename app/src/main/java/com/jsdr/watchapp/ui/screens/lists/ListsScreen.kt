package com.jsdr.watchapp.ui.screens.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jsdr.watchapp.BrandPurple
import com.jsdr.watchapp.DarkBackground
import com.jsdr.watchapp.ui.components.ListButton
import com.jsdr.watchapp.ui.navigation.Screen

@Composable
fun ListsScreen(
    navController: NavController,
    viewModel: ListsViewModel = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }
    var newListDescription by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        viewModel.loadLists()
    }
    Box(
        modifier = Modifier.fillMaxSize().background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Moje Listy",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (viewState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandPurple)
                }
            } else if (viewState.error != null) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = "Błąd: ${viewState.error}", color = Color.Red)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewState.usersLists) { list ->
                        ListButton(
                            userList = list,
                            onClick = {
                                navController.navigate(
                                    Screen.ListDetails.createRoute(
                                        list.name,
                                        list.description ?: "no description"
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 16.dp)
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BrandPurple)
                .clickable { showDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Dodaj listę",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                containerColor = DarkBackground,
                title = { Text(text = "Nowa lista", color = Color.White) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newListName,
                            onValueChange = { newListName = it },
                            label = { Text("Nazwa listy") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandPurple,
                                focusedLabelColor = BrandPurple,
                                cursorColor = BrandPurple
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = newListDescription,
                            onValueChange = { newListDescription = it },
                            label = { Text("Opis listy") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandPurple,
                                focusedLabelColor = BrandPurple,
                                cursorColor = BrandPurple
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newListName.isNotBlank()) {
                                viewModel.createList(newListName, newListDescription)
                            }
                            newListName = ""
                            newListDescription = ""
                            showDialog = false
                        }
                    ) {
                        Text(text = "Utwórz", color = BrandPurple)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            newListName = ""
                            newListDescription = ""
                            showDialog = false
                        }
                    ) {
                        Text(text = "Anuluj", color = Color.Gray)
                    }
                }
            )
        }
    }
}