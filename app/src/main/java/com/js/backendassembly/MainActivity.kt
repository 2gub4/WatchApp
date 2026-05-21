package com.js.backendassembly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
// Zakomentowane importy z uwagi na usunięcie EndpointType i przebudowę ApiManager
// import com.js.backendassembly.data.api.ApiManager
// import com.js.backendassembly.data.api.EndpointType
import com.js.backendassembly.data.models.dtos.movies.MovieOverviewDto
import com.js.backendassembly.data.repository.MoviesRepository
import com.js.backendassembly.domain.models.profiles.MovieProfile
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
//                        Box(modifier = Modifier.weight(1f)) {
//                            ApiTesterScreen()
//                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(100.dp))
                                ProfileSection()
                                HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)
                            }
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                                MoviesListSection()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSection() {
    val coroutineScope = rememberCoroutineScope()
    var movieProfile by remember { mutableStateOf<MovieProfile?>(null) }
    var profileStatus by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Button(
//            onClick = {
//                coroutineScope.launch {
//                    seedingStatus = "Trwa seeding..."
//                    MovieFirestore.initialSeeding()
//                    seedingStatus = "Seeding zakończony!"
//                }
//            },
//            modifier = Modifier.fillMaxWidth().height(50.dp)
//        ) {
//            Text("Wykonaj Firebase Initial Seeding", fontSize = 16.sp, fontWeight = FontWeight.Bold)
//        }
//
//        if (seedingStatus.isNotEmpty()) {
//            Text(text = seedingStatus, modifier = Modifier.padding(top = 4.dp), color = Color.Gray)
//        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    profileStatus = "Pobieranie profilu..."
                    val profile = MoviesRepository.getMovieProfile(803796)
                    if (profile != null) {
                        movieProfile = profile
                        profileStatus = "Pobrano pomyślnie!"
                    } else {
                        profileStatus = "Błąd: Nie udało się pobrać profilu."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Pobierz profil filmu (ID: 803796)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (profileStatus.isNotEmpty()) {
            Text(text = profileStatus, modifier = Modifier.padding(top = 4.dp), color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Movie Profile
        movieProfile?.let { profile ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 700.dp) // Zostawiamy ograniczenie wysokości, jeśli to konieczne
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                    // Usunięto verticalScroll z karty, ponieważ nadrzędny Column zmienił się w LazyColumn.
                    // Zagnieżdżone scrolle w tym samym kierunku powodują problemy.
                ) {
                    AsyncImage(model="${MoviesRepository.POSTERS_BASE_URL}${profile.movieDetails.posterPath}", contentDescription = "${profile.movieDetails.title} poster")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Tytuł: ${profile.movieDetails.title}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Język: ${profile.movieDetails.originalLanguage}", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Własna ocena: ${profile.rating?.overallRating ?: "Brak"}", color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Znajduje się w listach: ${if (profile.containingLists.isEmpty()) "Brak" else profile.containingLists.joinToString()}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Opis: ${profile.movieDetails.overview}", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Obsada: ${profile.getTop5Actors().joinToString { it.name }}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Reżyseria: ${profile.getDirector().name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun MoviesListSection() {
    val coroutineScope = rememberCoroutineScope()
    var moviesList by remember { mutableStateOf<List<MovieOverviewDto>>(emptyList()) }
    var listStatus by remember { mutableStateOf("") }
    var listLength by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    listStatus = "Pobieranie listy..."
                    // Wywołanie repozytorium z listType "now_playing" i stroną 1
                    val moviesPage = MoviesRepository.getMoviePage(pageNumber = 1, listType = "now_playing")

                    if (moviesPage != null && moviesPage.results.isNotEmpty()) {
                        moviesList = moviesPage.results
                        listLength = moviesPage.results.size
                        listStatus = "Pobrano pomyślnie!"
                    } else {
                        moviesList = emptyList()
                        listLength = 0
                        listStatus = "Brak danych do wyświetlenia."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Teraz grane filmy (Strona 1)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (listStatus.isNotEmpty()) {
            Text(text = listStatus, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
        }

        if (listLength > 0) {
            Text(
                text = "Ilość filmów na stronie: $listLength",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Ręczne wypisanie elementów (ponieważ LazyColumn w LazyColumn to problem w Compose)
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                moviesList.forEach { movie ->
                    MovieOverviewItem(movie = movie)
                }
            }
        }
    }
}

@Composable
fun MovieOverviewItem(movie: MovieOverviewDto) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { /* Brak akcji zgodnie z poleceniem */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Obrazek
            if (movie.posterPath != null) {
                AsyncImage(
                    model = "${MoviesRepository.POSTERS_BASE_URL}${movie.posterPath}",
                    contentDescription = "Plakat ${movie.title}",
                    modifier = Modifier
                        .size(width = 60.dp, height = 90.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(width = 60.dp, height = 90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Brak", fontSize = 10.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Tytuł
            Text(
                text = movie.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

enum class QueryMode(val displayName: String) {
    MOVIE("movie (JSON)"),
    LIST("watchlist (JSON)"),
    POSTER("poster (image)"),
    HOMEPAGE_LIST("app's homepage movies (JSON)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Deprecated("Replaced with ApiTesterScreen")
fun ApiTesterScreen() {
    var endpointInput by remember { mutableStateOf("popular") }
    var resultText by remember { mutableStateOf("result will appear here") }
    var requestedUrl by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf(QueryMode.MOVIE) }
    var currentImageUrl by remember { mutableStateOf<String?>(null) }
    val pageNumber = "1"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedMode.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("resource", fontSize = 18.sp) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                textStyle = TextStyle(fontSize = 24.sp),
                modifier = Modifier
                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .fillMaxWidth()
                    .height(80.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                QueryMode.entries.forEach { mode ->
                    DropdownMenuItem(
                        text = {
                            Text(mode.displayName, fontSize = 22.sp, modifier = Modifier.padding(vertical = 8.dp))
                        },
                        onClick = {
                            selectedMode = mode
                            expanded = false
                            endpointInput = when (mode) {
                                QueryMode.MOVIE -> "11"
                                QueryMode.LIST -> "1"
                                QueryMode.POSTER -> "/pWVLFh4OuejTpUaDQbB1C4zoS2p.jpg"
                                QueryMode.HOMEPAGE_LIST -> "now_playing?page=$pageNumber"
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = endpointInput,
            onValueChange = { endpointInput = it },
            label = { Text("movie_id, poster_path or list_id", fontSize = 18.sp) },
            textStyle = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                requestedUrl = ""
                currentImageUrl = null

                // ZAKOMENTOWANE: Ze względu na usunięcie EndpointType i przebudowę komunikacji z API.
                // Kiedy zaimplementujesz nowe podejście (np. bezpośrednie wołanie MovieApi w repozytorium),
                // możesz tutaj wstrzyknąć nowe metody.
                /*
                resultText = "processing..."
                val endpointType = when (selectedMode) {
                    QueryMode.MOVIE -> EndpointType.MOVIE
                    QueryMode.LIST -> EndpointType.LIST
                    QueryMode.POSTER -> EndpointType.POSTER
                    QueryMode.HOMEPAGE_LIST -> EndpointType.HOMEPAGE_LIST
                }

                coroutineScope.launch {
                    val result = ApiManager.fetchApiData(endpointType, endpointInput)
                    result.fold(
                        onSuccess = { apiResult ->
                            requestedUrl = apiResult.fullUrl
                            if (apiResult.isImage) {
                                currentImageUrl = apiResult.fullUrl
                                resultText = ""
                            } else {
                                currentImageUrl = null
                                resultText = apiResult.responseText
                            }
                        },
                        onFailure = { error ->
                            resultText = "ERROR:\n${error.localizedMessage}"
                        }
                    )
                }
                */

                // TYMCZASOWE ZACHOWANIE:
                resultText = "Testowanie API zostało wstrzymane (brak EndpointType). Zaktualizuj logikę zgodnie z nową architekturą."
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text("test API call", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            if (requestedUrl.isNotEmpty()) {
                Text(
                    text = "destination url:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = requestedUrl,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (currentImageUrl != null) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = currentImageUrl,
                        contentDescription = "recieved image",
                        modifier = Modifier.wrapContentHeight()
                    )
                    AsyncImage(
                        model = currentImageUrl,
                        contentDescription = "recieved image",
                        modifier = Modifier.wrapContentHeight()
                    )
                    AsyncImage(
                        model = currentImageUrl,
                        contentDescription = "recieved image",
                        modifier = Modifier.wrapContentHeight()
                    )
                }
            } else {
                Text(
                    text = resultText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}