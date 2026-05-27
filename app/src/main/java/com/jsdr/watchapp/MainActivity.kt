package com.jsdr.watchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
// Zakomentowane importy z uwagi na usunięcie EndpointType i przebudowę ApiManager
// import com.jsdr.watchapp.data.api.ApiManager
// import com.jsdr.watchapp.data.api.EndpointType
import com.jsdr.watchapp.data.models.dtos.movies.MovieOverviewDto
import com.jsdr.watchapp.data.models.dtos.shows.TvSeriesOverviewDto
import com.jsdr.watchapp.data.repository.WatchAppRepository
import com.jsdr.watchapp.domain.models.profiles.MovieProfile
import com.jsdr.watchapp.domain.models.profiles.TvSeriesProfile
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // Stan kontrolujący, który profil jest aktualnie otwarty
                var selectedMovieId by remember { mutableStateOf<Int?>(null) }
                var selectedSeriesId by remember { mutableStateOf<Int?>(null) }

                // Przejmujemy systemowy przycisk "Wstecz" na telefonie, aby zamykał okno profilu
                BackHandler(enabled = selectedMovieId != null || selectedSeriesId != null) {
                    selectedMovieId = null
                    selectedSeriesId = null
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Box pozwala nam nakładać widoki na siebie (jak warstwy w Photoshopie)
                    Box(modifier = Modifier.fillMaxSize()) {

                        // WARSTWA 1: Główna zawartość (Lista). Zawsze tu jest, więc pamięta pozycję scrolla!
                        Column(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                item {
                                    Spacer(modifier = Modifier.height(60.dp))
                                    ProfileSection()
                                    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                }
                                item {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    // Przekazujemy funkcję kliknięcia (zmienia ona nasz stan na wybrane ID)
                                    MoviesListSection(
                                        onMovieClick = { clickedId -> selectedMovieId = clickedId }
                                    )
                                    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 16.dp))
                                }
                                item {
                                    TvProfileSection()
                                    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 16.dp))
                                }
                                item {
                                    TvSeriesListSection(
                                        onSeriesClick = { clickedId -> selectedSeriesId = clickedId }
                                    )
                                    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 16.dp))
                                }
                                item {
                                    SearchSection(
                                        onMovieClick = { clickedId -> selectedMovieId = clickedId },
                                        onSeriesClick = { clickedId -> selectedSeriesId = clickedId }
                                    )
                                }
                            }
                        }

                        // WARSTWA 2: Nowe okno z profilem filmu (Wyświetla się tylko, gdy klikniemy film)
                        selectedMovieId?.let { movieId ->
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                DynamicMovieProfileScreen(
                                    movieId = movieId,
                                    onBack = { selectedMovieId = null } // Zamknięcie okna
                                )
                            }
                        }

                        // WARSTWA 3: Nowe okno z profilem serialu (Wyświetla się tylko, gdy klikniemy serial)
                        selectedSeriesId?.let { seriesId ->
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                DynamicTvProfileScreen(
                                    seriesId = seriesId,
                                    onBack = { selectedSeriesId = null } // Zamknięcie okna
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// DYNAMICZNE EKRANY PROFILOWE (OTWIERANE PO KLIKNIĘCIU)
// ==========================================

@Composable
fun DynamicMovieProfileScreen(movieId: Int, onBack: () -> Unit) {
    var movieProfile by remember { mutableStateOf<MovieProfile?>(null) }
    var status by remember { mutableStateOf("Ładowanie profilu...") }

    // Wywołuje się automatycznie po otwarciu ekranu
    LaunchedEffect(movieId) {
        val profile = WatchAppRepository.Movies.getMovieProfile(movieId)
        if (profile != null) {
            movieProfile = profile
            status = ""
        } else {
            status = "Błąd: Nie udało się pobrać profilu."
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Górny pasek z przyciskiem powrotu
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Wróć")
            }
            Text("Szczegóły filmu", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider()

        if (status.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = status, color = Color.Gray)
            }
        }

        movieProfile?.let { profile ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = "${WatchAppRepository.POSTERS_BASE_URL}${profile.movieDetails.posterPath}",
                    contentDescription = "${profile.movieDetails.title} poster",
                    modifier = Modifier.fillMaxWidth().height(400.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = profile.movieDetails.title, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Język oryginału: ${profile.movieDetails.originalLanguage}", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Własna ocena: ${profile.rating?.overallRating ?: "Brak"}", color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Opis: ${profile.movieDetails.overview}", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Obsada: ${profile.getTop5Actors().joinToString { it.name }}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Reżyseria: ${profile.getDirector()?.name ?: "Brak"}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun DynamicTvProfileScreen(seriesId: Int, onBack: () -> Unit) {
    var tvProfile by remember { mutableStateOf<TvSeriesProfile?>(null) }
    var status by remember { mutableStateOf("Ładowanie profilu...") }

    LaunchedEffect(seriesId) {
        val profile = WatchAppRepository.TvSeries.getTvSeriesProfile(seriesId)
        if (profile != null) {
            tvProfile = profile
            status = ""
        } else {
            status = "Błąd: Nie udało się pobrać profilu."
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Wróć")
            }
            Text("Szczegóły serialu", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider()

        if (status.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = status, color = Color.Gray)
            }
        }

        tvProfile?.let { profile ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = "${WatchAppRepository.POSTERS_BASE_URL}${profile.seriesDetails.posterPath}",
                    contentDescription = "${profile.seriesDetails.title} poster",
                    modifier = Modifier.fillMaxWidth().height(400.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = profile.seriesDetails.title, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Ilość sezonów: ${profile.seriesDetails.numberOfSeasons}", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Własna ocena: ${profile.rating?.overallRating ?: "Brak"}", color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Opis: ${profile.seriesDetails.overview}", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Obsada: ${profile.getTop10Actors().joinToString { it.name }}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Twórca: ${profile.seriesDetails.createdBy.firstOrNull()?.name ?: "Brak"}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}


// ==========================================
// SEKCJE ISTNIEJĄCE
// ==========================================

@Composable
fun ProfileSection() {
    val coroutineScope = rememberCoroutineScope()
    var movieProfile by remember { mutableStateOf<MovieProfile?>(null) }
    var profileStatus by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    profileStatus = "Pobieranie profilu..."
                    val profile = WatchAppRepository.Movies.getMovieProfile(11)
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
            Text("Pobierz profil filmu (ID: 11)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (profileStatus.isNotEmpty()) {
            Text(text = profileStatus, modifier = Modifier.padding(top = 4.dp), color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(8.dp))

        movieProfile?.let { profile ->
            Card(modifier = Modifier.fillMaxWidth().heightIn(max = 700.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    AsyncImage(model="${WatchAppRepository.POSTERS_BASE_URL}${profile.movieDetails.posterPath}", contentDescription = "${profile.movieDetails.title} poster")
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
                    Text(text = "Reżyseria: ${profile.getDirector()?.name ?: "Brak"}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun TvProfileSection() {
    val coroutineScope = rememberCoroutineScope()
    var tvProfile by remember { mutableStateOf<TvSeriesProfile?>(null) }
    var profileStatus by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    profileStatus = "Pobieranie profilu serialu..."
                    val profile = WatchAppRepository.TvSeries.getTvSeriesProfile(1399) // 1399 To ID Gry o Tron
                    if (profile != null) {
                        tvProfile = profile
                        profileStatus = "Pobrano pomyślnie!"
                    } else {
                        profileStatus = "Błąd: Nie udało się pobrać profilu."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Pobierz profil serialu (ID: 1399)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (profileStatus.isNotEmpty()) {
            Text(text = profileStatus, modifier = Modifier.padding(top = 4.dp), color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(8.dp))

        tvProfile?.let { profile ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    AsyncImage(model="${WatchAppRepository.POSTERS_BASE_URL}${profile.seriesDetails.posterPath}", contentDescription = "${profile.seriesDetails.title} poster")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Tytuł: ${profile.seriesDetails.title}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Sezony: ${profile.seriesDetails.numberOfSeasons}", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Odcinki: ${profile.seriesDetails.numberOfEpisodes}", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Własna ocena: ${profile.rating?.overallRating ?: "Brak"}", color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Opis: ${profile.seriesDetails.overview}", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Obsada: ${profile.getTop10Actors().joinToString { it.name }}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Twórca: ${profile.seriesDetails.createdBy.firstOrNull()?.name ?: "Brak"}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}


// ==========================================
// ZAKTUALIZOWANE LISTY WIDOKÓW Z PRZEKAZYWANIEM KLIKNIĘĆ
// ==========================================

@Composable
fun MoviesListSection(onMovieClick: (Int) -> Unit) { // Odbieramy akcję z góry
    val coroutineScope = rememberCoroutineScope()
    var moviesList by remember { mutableStateOf<List<MovieOverviewDto>>(emptyList()) }
    var listStatus by remember { mutableStateOf("") }
    var listLength by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    listStatus = "Pobieranie listy..."
                    val moviesPage = WatchAppRepository.Movies.getMoviePage(pageNumber = 1, listType = "now_playing")
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
        if (listStatus.isNotEmpty()) Text(text = listStatus, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))

        if (listLength > 0) {
            Text(text = "Ilość filmów na stronie: $listLength", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                moviesList.forEach { movie ->
                    // Wywołujemy kliknięcie przekazując ID konkretnego filmu
                    MovieOverviewItem(movie = movie, onClick = { onMovieClick(movie.id) })
                }
            }
        }
    }
}

@Composable
fun TvSeriesListSection(onSeriesClick: (Int) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var seriesList by remember { mutableStateOf<List<TvSeriesOverviewDto>>(emptyList()) }
    var listStatus by remember { mutableStateOf("") }
    var listLength by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    listStatus = "Pobieranie listy..."
                    val seriesPage = WatchAppRepository.TvSeries.getTvSeriesPage(pageNumber = 1, listType = "popular")

                    if (seriesPage != null && seriesPage.results.isNotEmpty()) {
                        seriesList = seriesPage.results
                        listLength = seriesPage.results.size
                        listStatus = "Pobrano pomyślnie!"
                    } else {
                        seriesList = emptyList()
                        listLength = 0
                        listStatus = "Brak danych do wyświetlenia."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Popularne seriale (Strona 1)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))
        if (listStatus.isNotEmpty()) Text(text = listStatus, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))

        if (listLength > 0) {
            Text(text = "Ilość seriali na stronie: $listLength", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                seriesList.forEach { series ->
                    TvSeriesOverviewItem(series = series, onClick = { onSeriesClick(series.id) })
                }
            }
        }
    }
}

@Composable
fun SearchSection(
    onMovieClick: (Int) -> Unit,
    onSeriesClick: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchForMovies by remember { mutableStateOf(true) }

    var moviesList by remember { mutableStateOf<List<MovieOverviewDto>>(emptyList()) }
    var tvSeriesList by remember { mutableStateOf<List<TvSeriesOverviewDto>>(emptyList()) }

    var searchStatus by remember { mutableStateOf("") }
    var listLength by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = {
                    isSearchForMovies = true
                    moviesList = emptyList()
                    tvSeriesList = emptyList()
                    listLength = 0
                    searchStatus = ""
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSearchForMovies) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = if (isSearchForMovies) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) { Text("Filmy") }

            Button(
                onClick = {
                    isSearchForMovies = false
                    moviesList = emptyList()
                    tvSeriesList = emptyList()
                    listLength = 0
                    searchStatus = ""
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isSearchForMovies) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = if (!isSearchForMovies) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) { Text("Seriale") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(if (isSearchForMovies) "Wyszukaj film" else "Wyszukaj serial") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (searchQuery.isNotBlank()) {
                    coroutineScope.launch {
                        searchStatus = "Wyszukiwanie..."
                        if (isSearchForMovies) {
                            val moviesPage = WatchAppRepository.Movies.searchForMovie(query = searchQuery, pageNumber = 1)
                            if (moviesPage != null && moviesPage.results.isNotEmpty()) {
                                moviesList = moviesPage.results
                                tvSeriesList = emptyList()
                                listLength = moviesPage.results.size
                                searchStatus = "Znaleziono pomyślnie!"
                            } else {
                                moviesList = emptyList()
                                tvSeriesList = emptyList()
                                listLength = 0
                                searchStatus = "Brak wyników."
                            }
                        } else {
                            val tvSeriesPage = WatchAppRepository.TvSeries.searchForTvSeries(query = searchQuery, pageNumber = 1)
                            if (tvSeriesPage != null && tvSeriesPage.results.isNotEmpty()) {
                                tvSeriesList = tvSeriesPage.results
                                moviesList = emptyList()
                                listLength = tvSeriesPage.results.size
                                searchStatus = "Znaleziono pomyślnie!"
                            } else {
                                tvSeriesList = emptyList()
                                moviesList = emptyList()
                                listLength = 0
                                searchStatus = "Brak wyników."
                            }
                        }
                    }
                } else {
                    searchStatus = "Wpisz coś w pole wyszukiwania."
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) { Text("Szukaj", fontSize = 16.sp, fontWeight = FontWeight.Bold) }

        Spacer(modifier = Modifier.height(8.dp))
        if (searchStatus.isNotEmpty()) Text(text = searchStatus, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))

        if (listLength > 0) {
            Text(text = "Ilość wyników wyszukiwania: $listLength", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                if (isSearchForMovies) {
                    moviesList.forEach { movie -> MovieOverviewItem(movie = movie, onClick = { onMovieClick(movie.id) }) }
                } else {
                    tvSeriesList.forEach { series -> TvSeriesOverviewItem(series = series, onClick = { onSeriesClick(series.id) }) }
                }
            }
        }
    }
}


// ==========================================
// KAFELKI POJEDYNCZE (ZOSTAŁY WZBOGACONE O ONCLICK)
// ==========================================

@Composable
fun MovieOverviewItem(movie: MovieOverviewDto, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            // Używamy akcji onClick przekazanej z góry (z Section)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (movie.posterPath != null) {
                AsyncImage(
                    model = "${WatchAppRepository.POSTERS_BASE_URL}${movie.posterPath}",
                    contentDescription = "Plakat ${movie.title}",
                    modifier = Modifier.size(width = 60.dp, height = 90.dp)
                )
            } else {
                Box(modifier = Modifier.size(width = 60.dp, height = 90.dp), contentAlignment = Alignment.Center) {
                    Text("Brak", fontSize = 10.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = movie.title, fontSize = 18.sp, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun TvSeriesOverviewItem(series: TvSeriesOverviewDto, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (series.posterPath != null) {
                AsyncImage(
                    model = "${WatchAppRepository.POSTERS_BASE_URL}${series.posterPath}",
                    contentDescription = "Plakat ${series.name}",
                    modifier = Modifier.size(width = 60.dp, height = 90.dp)
                )
            } else {
                Box(modifier = Modifier.size(width = 60.dp, height = 90.dp), contentAlignment = Alignment.Center) {
                    Text("Brak", fontSize = 10.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = series.name, fontSize = 18.sp, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        }
    }
}

enum class QueryMode(val displayName: String) {
    MOVIE("movie (JSON)"),
    LIST("watchlist (JSON)"),
    POSTER("poster (image)"),
    HOMEPAGE_LIST("app's homepage movies (JSON)")
}