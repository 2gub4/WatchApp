package com.jsdr.watchapp.ui.screens.profile
import com.jsdr.watchapp.BrandPurple
import com.jsdr.watchapp.DarkBackground
import com.jsdr.watchapp.ui.navigation.Screen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import androidx.compose.animation.core.animateDpAsState

@Composable
fun ProfileScreen(
    navController: NavController
) {

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

    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 18.dp),

            verticalArrangement = Arrangement.spacedBy(18.dp),

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

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(BrandPurple),

                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "👤",
                            fontSize = 50.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // NAZWA
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

            item {

                ProfileInfoItem(
                    title = "Email",
                    value = email,
                    onClick = {
                        showEmailDialog = true
                    }
                )
            }

            item {

                ProfileInfoItem(
                    title = "Rok urodzenia",
                    value = birthYear,
                    onClick = {
                        showBirthDialog = true
                    }
                )
            }

            item {

                ProfileInfoItem(
                    title = "Płeć",
                    value = gender,
                    onClick = {
                        showGenderDialog = true
                    }
                )
            }

            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(BrandPurple)
                        .clickable {

                            navController.navigate(
                                Screen.Statistics.route
                            )
                        }
                        .padding(20.dp),

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

        SpotifyScrollbarList(
            listState = listState,

            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight()
        )
    }


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
fun ProfileInfoItem(
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
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
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

                modifier = Modifier.fillMaxWidth(),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPurple,
                    unfocusedBorderColor = BrandPurple,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
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
fun SpotifyScrollbarList(
    listState: androidx.compose.foundation.lazy.LazyListState,
    modifier: Modifier = Modifier
) {

    var isDragging by remember {
        mutableStateOf(false)
    }

    val thumbWidth by animateDpAsState(
        targetValue = if (isDragging) 8.dp else 4.dp,
        label = ""
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
        } else 0f

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
                                (totalItemsCount.toFloat() / visibleItemsCount) * 1.5f
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
                    if (isDragging) Color.White else BrandPurple
                )
                .align(Alignment.TopEnd)
        )
    }
}