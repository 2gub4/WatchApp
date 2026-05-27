package com.jsdr.watchapp.ui.screens.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController

import com.jsdr.watchapp.BrandPurple
import com.jsdr.watchapp.DarkBackground
import com.jsdr.watchapp.data.models.entities.UserList
import com.jsdr.watchapp.ui.navigation.Screen

@Composable
fun ListsScreen(navController: NavController) {

    // LISTY DOMYŚLNE
    val defaultLists = listOf(

        UserList(
            name = "Ulubione",
            description = "Filmy które polubiłeś najbardziej"
        ),

        UserList(
            name = "Do obejrzenia",
            description = "Filmy do obejrzenia"
        )
    )

    // LISTY UŻYTKOWNIKA
    val movieLists = remember {
        mutableStateListOf<UserList>()
    }

    var showDialog by remember { mutableStateOf(false) }

    var newListName by remember {
        mutableStateOf("")
    }

    var newListDescription by remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier.fillMaxSize()
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

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // DOMYŚLNE LISTY
                defaultLists.forEach { list ->

                    ListButton(
                        UserList = list,
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

                // LISTY UŻYTKOWNIKA
                movieLists.forEach { list ->

                    ListButton(
                        UserList = list,
                        onClick = {

                            navController.navigate(
                                Screen.ListDetails.createRoute(
                                    list.name,
                                    list.description  ?: "no description"
                                )
                            )
                        }
                    )
                }
            }
        }

        // FAB
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 16.dp)
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BrandPurple)
                .clickable {
                    showDialog = true
                },

            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Dodaj listę",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // DIALOG
        if (showDialog) {

            AlertDialog(

                onDismissRequest = {
                    showDialog = false
                },

                containerColor = DarkBackground,

                title = {

                    Text(
                        text = "Nowa lista",
                        color = Color.White
                    )
                },

                text = {

                    Column {

                        // NAZWA
                        OutlinedTextField(

                            value = newListName,

                            onValueChange = {
                                newListName = it
                            },

                            label = {
                                Text("Nazwa listy")
                            },

                            singleLine = true,

                            modifier = Modifier.fillMaxWidth(),

                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandPurple,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = BrandPurple,
                                unfocusedLabelColor = Color.Gray,
                                cursorColor = BrandPurple
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // OPIS
                        OutlinedTextField(

                            value = newListDescription,

                            onValueChange = {
                                newListDescription = it
                            },

                            label = {
                                Text("Opis listy")
                            },

                            modifier = Modifier.fillMaxWidth(),

                            minLines = 2,
                            maxLines = 3,

                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandPurple,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = BrandPurple,
                                unfocusedLabelColor = Color.Gray,
                                cursorColor = BrandPurple
                            )
                        )
                    }
                },

                confirmButton = {

                    TextButton(

                        onClick = {

                            if (
                                newListName.isNotBlank() &&
                                movieLists.none { it.name == newListName }
                            ) {

                                movieLists.add(

                                    UserList(
                                        name = newListName,
                                        description = newListDescription
                                    )
                                )
                            }

                            newListName = ""
                            newListDescription = ""

                            showDialog = false
                        }
                    ) {

                        Text(
                            text = "Utwórz",
                            color = BrandPurple
                        )
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

                        Text(
                            text = "Anuluj",
                            color = Color.Gray
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun ListButton(
    UserList: UserList,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BrandPurple)
            .clickable {
                onClick()
            }
            .padding(20.dp)
    ) {

        Text(
            text = UserList.name,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}