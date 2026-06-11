package com.jsdr.watchapp.data.repository

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.jsdr.watchapp.data.api.TmdbApiResult
import com.jsdr.watchapp.data.api.TmdbApi
import com.jsdr.watchapp.data.firebase.WatchAppFirestore
import com.jsdr.watchapp.data.models.dtos.UserDto
import com.jsdr.watchapp.domain.models.profiles.MovieProfile
import com.jsdr.watchapp.data.models.dtos.movies.MovieDetailsDto
import com.jsdr.watchapp.data.models.dtos.movies.MoviesPageDto
import com.jsdr.watchapp.data.models.dtos.shows.TvSeriesDetailsDto
import com.jsdr.watchapp.data.models.dtos.shows.TvSeriesPageDto
import com.jsdr.watchapp.domain.models.profiles.TvSeriesProfile
import com.jsdr.watchapp.data.models.entities.User
import com.jsdr.watchapp.data.models.entities.UserList
import com.jsdr.watchapp.domain.models.MediaOverview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object WatchAppRepository {

    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    const val POSTERS_BASE_URL = "https://image.tmdb.org/t/p/w500"
    private val auth = Firebase.auth
    private val _currentUserId = MutableStateFlow(auth.currentUser?.uid)

    val currentUserFlow: StateFlow<String?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }.stateIn(
        scope = repoScope,
        started = SharingStarted.Lazily,
        initialValue = auth.currentUser?.uid
    )

    val requireUserId: String get() = auth.currentUser?.uid ?: ""

    object Auth {
        suspend fun signIn(email: String, password: String): Result<Boolean> {
            return try {
                auth.signInWithEmailAndPassword(email, password).await()
                Result.success(true)
            } catch (e: Exception) {
                Log.e("AuthRepository", "Could not sign user in", e)
                Result.failure(e)
            }
        }

        fun signOut() {
            auth.signOut()
        }

        suspend fun registerUser( email: String, password: String, username: String, birthYear: Int, gender: String): Result<Boolean> {
            return try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val newUser = User(
                        uid = firebaseUser.uid,
                        email = email,
                        username = username,
                        birthYear = birthYear,
                        gender = gender
                    )
                    WatchAppFirestore.Users.addUser(newUser)
                    Result.success(true)
                } else {
                    Result.failure(Exception("Could not register user."))
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", "Could not register user", e)
                Result.failure(e)
            }
        }
    }

    object User {
        suspend fun addNewUser(userDto: UserDto) {
            val newUser = User(
                requireUserId,
                userDto.email,
                userDto.username,
                null,
                userDto.birthYear,
                userDto.gender,
                userDto.pfpPath
            )
            WatchAppFirestore.Users.addUser(newUser)
        }

        suspend fun getUser(userId: String? = requireUserId): com.jsdr.watchapp.data.models.entities.User? {
            return when (userId) {
                null -> null
                else -> WatchAppFirestore.Users.getCurrentUser(userId)
            }
        }

        suspend fun getUserStats(): Map<String, Double>? {
            if (requireUserId.isEmpty()) return null
            return withContext(Dispatchers.IO) {
                val stats = async { WatchAppFirestore.Users.getUserStats(requireUserId) }
                stats.await()
            }
        }

        suspend fun update(subject: String, newValue: String) {
            when (subject) {
                "username" -> WatchAppFirestore.Users.Updates.updateUsername(requireUserId, newValue)
                "gender" -> WatchAppFirestore.Users.Updates.updateGender(requireUserId, newValue)
                "birthYear" -> WatchAppFirestore.Users.Updates.updateBirthYear(requireUserId, newValue.toInt())
                "email" -> WatchAppFirestore.Users.Updates.updateEmail(requireUserId, newValue)
                else -> throw Exception("Illegal argument: $subject")
            }
        }
    }

    object Lists {
        suspend fun getUserLists(): List<UserList> {
            return WatchAppFirestore.Users.getUserLists(requireUserId)
        }

        suspend fun createList(list: UserList) {
            WatchAppFirestore.Lists.createUserList(requireUserId, list)
        }

        suspend fun deleteList(listId: String) {
            WatchAppFirestore.Lists.deleteUserList(requireUserId, listId)
        }

        suspend fun removeMediaFromList(listId: String, mediaId: Int, isMovie: Boolean) {
            WatchAppFirestore.Media.removeMediaFromList(requireUserId, listId, mediaId, isMovie)
        }

        suspend fun getMediaOverviewsForList(userList: UserList): List<MediaOverview> {
            return withContext(Dispatchers.IO) {
                val movieItems = userList.movies.entries.map { Triple(it.key.toInt(), it.value, true) }
                val seriesItems = userList.series.entries.map { Triple(it.key.toInt(), it.value, false) }

                val combinedSortedItems = (movieItems + seriesItems).sortedByDescending { it.second }

                val deferredItems = combinedSortedItems.map { item ->
                    val mediaId = item.first
                    val isMovie = item.third
                    async {
                        if (isMovie) {
                            val details = Movies.getApiMovieDetails(mediaId)
                            if (details != null) {
                                MediaOverview(
                                    id = details.id,
                                    title = details.title,
                                    posterPath = details.posterPath,
                                    releaseDate = details.releaseDate,
                                    isMovie = true
                                )
                            } else null
                        } else {
                            val details = TvSeries.getApiTvSeriesDetails(mediaId)
                            if (details != null) {
                                MediaOverview(
                                    id = details.id,
                                    title = details.title,
                                    posterPath = details.posterPath,
                                    releaseDate = details.firstAired,
                                    isMovie = false
                                )
                            } else null
                        }
                    }
                }

                val resultList = mutableListOf<MediaOverview>()
                for (deferred in deferredItems) {
                    val media = deferred.await()
                    if (media != null) {
                        resultList.add(media)
                    }
                }
                resultList
            }
        }

        suspend fun changeListName(listId: String, newName: String) {
            if (requireUserId.isEmpty()) return
            WatchAppFirestore.Lists.Updates.changeListName(requireUserId, listId, newName)
        }

        suspend fun changeListColor(listId: String, newColor: Color) {
            WatchAppFirestore.Lists.Updates.changeListColor(requireUserId, listId, newColor)
        }
    }

    suspend fun addMediaToList(listId: String, mediaId: Int, isMovie: Boolean) {
        WatchAppFirestore.Media.addMediaToList(requireUserId, listId, mediaId, isMovie)
    }

    object Movies {
        suspend fun getApiMovieDetails(movieId: Int): MovieDetailsDto? {
            return when (val response = TmdbApi.MoviesData.fetchMovieDetails(movieId)) {
                is TmdbApiResult.OnSuccess -> response.data
                is TmdbApiResult.OnFailure -> {
                    Log.e("Movie Repository", "Could not recieve MovieDetailsDto", response.error)
                    null
                }
            }
        }

        suspend fun getMovieProfile(movieId: Int): MovieProfile? {
            return withContext(Dispatchers.IO) {
                val apiMovieDetails = async { getApiMovieDetails(movieId) }
                val potentialUserRating = async { WatchAppFirestore.Ratings.getMediaRating(requireUserId, movieId, true) }
                val containingLists = async { WatchAppFirestore.Lists.getListsContainingMedia(requireUserId, movieId, true) }
                val details = apiMovieDetails.await() ?: return@withContext null
                MovieProfile(
                    movieDetails = details,
                    rating = potentialUserRating.await(),
                    containingLists = containingLists.await()
                )
            }
        }

        suspend fun getMoviePage(pageNumber: Int, listType: String): MoviesPageDto? {
            return withContext(Dispatchers.IO) {
                when (val response = TmdbApi.MoviesData.fetchMoviesPage(listType, pageNumber)) {
                    is TmdbApiResult.OnSuccess -> response.data
                    is TmdbApiResult.OnFailure -> {
                        Log.e("Movie Repository", "Could not recieve MoviesPageDto", response.error)
                        null
                    }
                }
            }
        }

        suspend fun searchForMovie(query: String, pageNumber: Int): MoviesPageDto? {
            return withContext(Dispatchers.IO) {
                when (val response = TmdbApi.MoviesData.fetchSearchedMovies(query, pageNumber)) {
                    is TmdbApiResult.OnSuccess -> response.data
                    is TmdbApiResult.OnFailure -> {
                        Log.e("Movie Repository", "Could not search movies", response.error)
                        null
                    }
                }
            }
        }
    }

    object TvSeries {
        suspend fun getApiTvSeriesDetails(seriesId: Int): TvSeriesDetailsDto? {
            return when (val response = TmdbApi.TvSeriesData.fetchTvSeriesDetails(seriesId)) {
                is TmdbApiResult.OnSuccess -> response.data
                is TmdbApiResult.OnFailure -> {
                    Log.e("Movie Repository", "Could not recieve MovieDetailsDto", response.error)
                    null
                }
            }
        }

        suspend fun getTvSeriesProfile(seriesId: Int): TvSeriesProfile? {
            return withContext(Dispatchers.IO) {
                val apiTvSeriesDetails = async { getApiTvSeriesDetails(seriesId) }
                val potentialUserRating = async { WatchAppFirestore.Ratings.getMediaRating(requireUserId, seriesId, false) }
                val containingLists = async { WatchAppFirestore.Lists.getListsContainingMedia(requireUserId, seriesId, false) }
                val details = apiTvSeriesDetails.await() ?: return@withContext null
                TvSeriesProfile(
                    seriesDetails = details,
                    rating = potentialUserRating.await(),
                    containingLists = containingLists.await()
                )
            }
        }

        suspend fun getTvSeriesPage(pageNumber: Int, listType: String): TvSeriesPageDto? {
            return withContext(Dispatchers.IO) {
                when (val response = TmdbApi.TvSeriesData.fetchTvSeriesPage(listType, pageNumber)) {
                    is TmdbApiResult.OnSuccess -> response.data
                    is TmdbApiResult.OnFailure -> {
                        Log.e("Movie Repository", "Could not recieve TvSeriesPageDto", response.error)
                        null
                    }
                }
            }
        }

        suspend fun searchForTvSeries(query: String, pageNumber: Int): TvSeriesPageDto? {
            return withContext(Dispatchers.IO) {
                when (val response = TmdbApi.TvSeriesData.fetchSearchedTvSeries(query, pageNumber)) {
                    is TmdbApiResult.OnSuccess -> response.data
                    is TmdbApiResult.OnFailure -> {
                        Log.e("Movie Repository", "Could not search movies", response.error)
                        null
                    }
                }
            }
        }
    }

    object Ratings {
        suspend fun saveRating(
            mediaId: Int,
            isMovie: Boolean,
            rating: com.jsdr.watchapp.data.models.entities.Rating
        ) {
            val existing = WatchAppFirestore.Ratings.getMediaRating(
                requireUserId,
                mediaId,
                isMovie
            )
            if (existing == null) {
                WatchAppFirestore.Ratings.addMediaRating(
                    requireUserId,
                    mediaId,
                    isMovie,
                    rating
                )
            } else {
                WatchAppFirestore.Ratings.updateMediaRating(
                    requireUserId,
                    mediaId,
                    isMovie,
                    rating
                )
            }
        }

        suspend fun getRating(
            mediaId: Int,
            isMovie: Boolean
        ) = WatchAppFirestore.Ratings.getMediaRating(
            requireUserId,
            mediaId,
            isMovie
        )

        suspend fun deleteRating(
            mediaId: Int,
            isMovie: Boolean
        ) {
            WatchAppFirestore.Ratings.deleteMediaRating(
                requireUserId,
                mediaId,
                isMovie
            )
        }
    }
}