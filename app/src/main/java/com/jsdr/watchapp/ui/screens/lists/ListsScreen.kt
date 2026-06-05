package com.jsdr.watchapp.ui.screens.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
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
import com.jsdr.watchapp.data.models.entities.UserList
import com.jsdr.watchapp.ui.navigation.Screen

@Composable
fun ListsScreen(
    navController: NavController,
    viewModel: ListsViewModel = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }
    var newListDescription by remember { mutableStateOf("") }
    var listToDelete by remember { mutableStateOf<UserList?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadLists()
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text("Moje Listy", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewState.usersLists) { list ->
                    val isDefault = list.id in listOf("favourites", "bucketlist", "watched")

                    ListButton(
                        userList = list,
                        showOptions = !isDefault,
                        onClick = {
                            if (list.description.isNullOrBlank()) list.description = "Brak opisu"
                            navController.navigate(Screen.ListDetails.createRoute(list.name, list.description!!))
                        },
                        onDeleteClick = {
                            listToDelete = list
                        }
                    )
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
                .clickable { showAddDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Dodaj listę",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                containerColor = DarkBackground,
                title = { Text("Nowa lista", color = Color.White) },
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
                    TextButton(onClick = {
                        if (newListName.isNotBlank()) {
                            viewModel.createList(newListName, newListDescription)
                        }
                        newListName = ""
                        newListDescription = ""
                        showAddDialog = false
                    }) {
                        Text("Utwórz", color = BrandPurple)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        newListName = ""
                        newListDescription = ""
                        showAddDialog = false
                    }) {
                        Text("Anuluj", color = Color.Gray)
                    }
                }
            )
        }
        if (listToDelete != null) {
            AlertDialog(
                onDismissRequest = { listToDelete = null },
                containerColor = DarkBackground,
                title = { Text("Usuń listę", color = Color.White) },
                text = { Text("Czy na pewno chcesz usunąć listę '${listToDelete?.name}'? Tej operacji nie można cofnąć.", color = Color.Gray) },
                confirmButton = {
                    TextButton(onClick = {
                        listToDelete?.let { viewModel.deleteList(it.id) }
                        listToDelete = null
                    }) {
                        Text("Usuń", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { listToDelete = null }) {
                        Text("Anuluj", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun ListButton(
    userList: UserList,
    showOptions: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BrandPurple)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = userList.name,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        if (showOptions) {
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Opcje", tint = Color.White)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(DarkBackground)
                ) {
                    DropdownMenuItem(
                        text = { Text("Usuń listę", color = Color.Red) },
                        onClick = {
                            expanded = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }
}