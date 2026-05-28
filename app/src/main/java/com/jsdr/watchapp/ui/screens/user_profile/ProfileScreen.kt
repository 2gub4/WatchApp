package com.jsdr.watchapp.ui.screens.user_profile
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlin.math.max
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.navigation.compose.rememberNavController
import com.jsdr.watchapp.ui.navigation.Screen

val DarkBackground = Color(0xFF212121)
val BrandPurple = Color(0xFF7F52FF)

@Composable
fun ProfileScreen(
    navController: NavController
) {

    val listState = rememberLazyListState()

    var username by remember {
        mutableStateOf("Nazwa użytkownika")
    }

    var email by remember {
        mutableStateOf("email@gmail.com")
    }

    var birthYear by remember {
        mutableStateOf("2000")
    }

    var gender by remember {
        mutableStateOf("Pan")
    }

    // KOLOR PROFILU

    val profileColor =
        if (gender == "Pani") {
            Color(0xFFFF4FA3)
        } else {
            Color(0xFF4DA6FF)
        }

    // DIALOGI

    var showUsernameDialog by remember {
        mutableStateOf(false)
    }

    var showEmailDialog by remember {
        mutableStateOf(false)
    }

    var showBirthDialog by remember {
        mutableStateOf(false)
    }

    var showGenderDialog by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),

            verticalArrangement = Arrangement.spacedBy(20.dp),

            contentPadding = PaddingValues(
                top = 20.dp,
                bottom = 120.dp
            )
        ) {

            item {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    // AVATAR

                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(profileColor),

                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "👤",
                            fontSize = 60.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // USERNAME

                    Text(
                        text = username,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,

                        modifier = Modifier.clickable {
                            showUsernameDialog = true
                        }
                    )
                }
            }

            // EMAIL

            item {

                ProfileInfoCard(
                    title = "Email",
                    value = email,
                    onClick = {
                        showEmailDialog = true
                    }
                )
            }

            // ROK URODZENIA

            item {

                ProfileInfoCard(
                    title = "Rok urodzenia",
                    value = birthYear,
                    onClick = {
                        showBirthDialog = true
                    }
                )
            }

            // PŁEĆ

            item {

                ProfileInfoCard(
                    title = "Płeć",
                    value = gender,
                    onClick = {
                        showGenderDialog = true
                    }
                )
            }

            // STATYSTYKI

            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(BrandPurple)
                        .clickable {
                            navController.navigate(Screen.Statistics.route)
                        }
                        .padding(24.dp),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Statystyki",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // SCROLLBAR

        SpotifyScrollbarProfile(
            listState = listState,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight()
        )
    }

    // DIALOG USERNAME

    if (showUsernameDialog) {

        EditDialog(
            title = "Nazwa użytkownika",
            currentValue = username,
            onDismiss = {
                showUsernameDialog = false
            },
            onConfirm = {
                username = it
                showUsernameDialog = false
            }
        )
    }

    // DIALOG EMAIL

    if (showEmailDialog) {

        EditDialog(
            title = "Email",
            currentValue = email,
            onDismiss = {
                showEmailDialog = false
            },
            onConfirm = {
                email = it
                showEmailDialog = false
            }
        )
    }

    // DIALOG ROK

    if (showBirthDialog) {

        EditDialog(
            title = "Rok urodzenia",
            currentValue = birthYear,
            onDismiss = {
                showBirthDialog = false
            },
            onConfirm = {
                birthYear = it
                showBirthDialog = false
            }
        )
    }

    // DIALOG PŁEĆ

    if (showGenderDialog) {

        AlertDialog(
            onDismissRequest = {
                showGenderDialog = false
            },

            containerColor = DarkBackground,

            title = {
                Text(
                    text = "Wybierz płeć",
                    color = Color.White
                )
            },

            text = {

                Column {

                    Text(
                        text = "Pan",
                        color = Color.White,
                        fontSize = 20.sp,

                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                gender = "Pan"
                                showGenderDialog = false
                            }
                            .padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Pani",
                        color = Color.White,
                        fontSize = 20.sp,

                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                gender = "Pani"
                                showGenderDialog = false
                            }
                            .padding(16.dp)
                    )
                }
            },

            confirmButton = {}
        )
    }
}

@Composable
fun ProfileInfoCard(
    title: String,
    value: String,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BrandPurple)
            .clickable {
                onClick()
            }
            .padding(20.dp)
    ) {

        Text(
            text = title,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EditDialog(
    title: String,
    currentValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {

    var text by remember {
        mutableStateOf(currentValue)
    }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },

        containerColor = DarkBackground,

        title = {
            Text(
                text = title,
                color = Color.White
            )
        },

        text = {

            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                },

                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = BrandPurple,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = BrandPurple
                )
            )
        },

        confirmButton = {

            TextButton(
                onClick = {
                    onConfirm(text)
                }
            ) {

                Text(
                    text = "OK",
                    color = BrandPurple
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = {
                    onDismiss()
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

@Composable
fun SpotifyScrollbarProfile(
    listState: LazyListState,
    modifier: Modifier = Modifier
) {

    var isDragging by remember {
        mutableStateOf(false)
    }

    val thumbWidth by animateDpAsState(
        targetValue = if (isDragging) 8.dp else 4.dp,
        label = "scrollbar_width"
    )

    val coroutineScope = rememberCoroutineScope()

    val layoutInfo = listState.layoutInfo
    val totalItemsCount = layoutInfo.totalItemsCount

    if (totalItemsCount == 0) return

    val firstVisibleItemIndex = listState.firstVisibleItemIndex
    val visibleItemsCount = layoutInfo.visibleItemsInfo.size

    val scrollProportion =
        if (totalItemsCount > visibleItemsCount) {
            firstVisibleItemIndex.toFloat() /
                    (totalItemsCount - visibleItemsCount).toFloat()
        } else {
            0f
        }

    val thumbSizeProportion =
        (visibleItemsCount.toFloat() / totalItemsCount.toFloat())
            .coerceIn(0.1f, 1f)

    BoxWithConstraints(
        modifier = modifier
            .width(32.dp)
            .pointerInput(Unit) {

                detectVerticalDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false
                    },
                    onDragCancel = {
                        isDragging = false
                    }
                ) { change, dragAmount ->

                    change.consume()

                    coroutineScope.launch {

                        val scrollMultiplier =
                            if (visibleItemsCount > 0) {
                                (totalItemsCount.toFloat() /
                                        visibleItemsCount) * 1.5f
                            } else {
                                5f
                            }

                        listState.scrollBy(
                            dragAmount * scrollMultiplier
                        )
                    }
                }
            }
    ) {

        val trackHeight = maxHeight.value

        val thumbHeight =
            max(trackHeight * thumbSizeProportion, 30f).dp

        val maxOffsetY = maxHeight - thumbHeight

        val offsetY =
            (maxOffsetY * scrollProportion)
                .coerceIn(0.dp, maxOffsetY)

        Box(
            modifier = Modifier
                .offset(y = offsetY)
                .width(thumbWidth)
                .height(thumbHeight)
                .clip(RoundedCornerShape(50))
                .background(
                    if (isDragging) {
                        Color.White
                    } else {
                        BrandPurple
                    }
                )
                .align(Alignment.TopEnd)
        )
    }
}