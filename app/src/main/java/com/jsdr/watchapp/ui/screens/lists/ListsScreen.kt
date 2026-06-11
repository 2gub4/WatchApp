package com.jsdr.watchapp.ui.screens.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jsdr.watchapp.BrandPurple
import com.jsdr.watchapp.DarkBackground
import com.jsdr.watchapp.data.models.entities.UserList
import com.jsdr.watchapp.data.models.toColor
import com.jsdr.watchapp.data.repository.WatchAppRepository
import com.jsdr.watchapp.ui.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun ListsScreen(
    navController: NavController,
    viewModel: ListsViewModel = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    val currentUser by WatchAppRepository.currentUserFlow.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showLoginRequiredDialog by remember { mutableStateOf(false) }
    var listToDelete by remember { mutableStateOf<UserList?>(null) }
    var listToRecolor by remember { mutableStateOf<UserList?>(null) }
    var listToEdit by remember { mutableStateOf<UserList?>(null) }

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
                items(viewState.usersLists/*.filter { it.id != "watched" }*/) { list ->
                    val isDefault = list.id in listOf("favourites", "bucketlist", "watched", "rated")
                    ListButton(
                        userList = list,
                        showOptions = !isDefault,
                        onClick = {
                            if (list.description.isNullOrBlank()) list.description = "Brak opisu"
                            navController.navigate(Screen.ListDetails.createRoute(list.name, list.description!!))
                        },
                        onEditClick = {
                            listToEdit = list
                        },
                        onDeleteClick = {
                            listToDelete = list
                        },
                        onColorChangeClick = {
                            listToRecolor = list
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
                .clickable {
                    if (currentUser == null) {
                        showLoginRequiredDialog = true
                    } else {
                        showAddDialog = true
                    }
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

        if (showAddDialog) {
            ListActionDialog(
                title = "Nowa lista",
                showDescriptionField = true,
                existingLists = viewState.usersLists,
                onDismiss = { showAddDialog = false },
                onConfirm = { name, description ->
                    viewModel.createList(name, description)
                    showAddDialog = false
                }
            )
        }

        listToEdit?.let { list ->
            ListActionDialog(
                title = "Zmień nazwę",
                initialName = list.name,
                initialDescription = list.description ?: "",
                showDescriptionField = false,
                existingLists = viewState.usersLists,
                currentListId = list.id,
                onDismiss = { listToEdit = null },
                onConfirm = { newName, _ ->
                    viewModel.renameList(list.id, newName)
                    listToEdit = null
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

        listToRecolor?.let { list ->
            ColorPickerDialog(
                onDismiss = { listToRecolor = null },
                onColorSelected = { newColor ->
                    viewModel.changeListColor(list.id, newColor)
                    listToRecolor = null
                }
            )
        }
    }

    if (showLoginRequiredDialog) {
        AlertDialog(
            onDismissRequest = {
                showLoginRequiredDialog = false
            },
            containerColor = DarkBackground,
            title = {
                Text(
                    text = "Brak dostępu",
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Musisz się zalogować, aby korzystać z tej funkcji.",
                    color = Color.White
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLoginRequiredDialog = false
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

@Composable
fun ListButton(
    userList: UserList,
    showOptions: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onColorChangeClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(userList.color.toColor())
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(16.dp))
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
                        text = { Text("Zmień nazwę", color = Color.White) },
                        onClick = {
                            expanded = false
                            onEditClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Zmień kolor", color = Color.White) },
                        onClick = {
                            expanded = false
                            onColorChangeClick()
                        }
                    )
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

@Composable
fun ColorPickerDialog(
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    val predefinedColors = listOf(
        BrandPurple, Color(0xFFF44336), Color(0xFFE91E63), Color(0xFF9C27B0),
        Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF03A9F4), Color(0xFF00BCD4),
        Color(0xFF009688), Color(0xFF4CAF50), Color(0xFF8BC34A), Color(0xFFFFEB3B),
        Color(0xFFFFC107), Color(0xFFFF9800), Color(0xFFFF5722), Color(0xFF795548)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBackground,
        title = { Text(text = "Wybierz kolor listy", color = Color.White) },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(predefinedColors) { color ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(color)
                            .clickable { onColorSelected(color) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj", color = Color.Gray)
            }
        }
    )
}

@Composable
fun ListActionDialog(
    title: String,
    initialName: String = "",
    initialDescription: String = "",
    showDescriptionField: Boolean = true,
    existingLists: List<UserList>,
    currentListId: String? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String) -> Unit
) {
    var listName by remember { mutableStateOf(initialName) }
    var listDescription by remember { mutableStateOf(initialDescription) }
    val isNameDuplicate = existingLists.any {
        it.name.equals(listName.trim(), ignoreCase = true) && it.id != currentListId
    }
    val isFormValid = listName.trim().isNotBlank() && !isNameDuplicate
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBackground,
        title = { Text(text = title, color = Color.White) },
        text = {
            Column {
                OutlinedTextField(
                    value = listName,
                    onValueChange = { listName = it },
                    label = { Text("Nazwa listy") },
                    singleLine = true,
                    isError = isNameDuplicate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        errorTextColor = Color.White,
                        focusedBorderColor = BrandPurple,
                        focusedLabelColor = BrandPurple,
                        cursorColor = BrandPurple,
                        errorBorderColor = Color.Red,
                        errorLabelColor = Color.Red,
                        errorCursorColor = Color.Red
                    )
                )
                if (isNameDuplicate) {
                    Text(
                        text = "Lista o tej nazwie już istnieje.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                if (showDescriptionField) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = listDescription,
                        onValueChange = { listDescription = it },
                        label = { Text("Opis (opcjonalnie)") },
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isFormValid) {
                        onConfirm(listName.trim(), listDescription.trim())
                    }
                },
                enabled = isFormValid
            ) {
                Text("Zapisz", color = if (isFormValid) BrandPurple else Color.Gray)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj", color = Color.Gray)
            }
        }
    )
}