package com.jsdr.watchapp.data.repository

import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

const val CURRENT_USER: String = "test_user"
//private val currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""


object WatchAppRepository {
    const val POSTERS_BASE_URL = "https://image.tmdb.org/t/p/w500"

    object User {

        suspend fun addNewUser(userDto: UserDto) {
            val newUser = User(
                CURRENT_USER,
                userDto.email,
                userDto.username,
                null,
                userDto.birthYear,
                userDto.gender,
                userDto.pfpPath
            )
            WatchAppFirestore.Users.addUser(newUser)
        }

        suspend fun getUser(userId: String = CURRENT_USER): com.jsdr.watchapp.data.models.entities.User? {
            return WatchAppFirestore.Users.getCurrentUser(userId)
        }

        suspend fun getUserStats(): Map<String, Double> /*change to ints in repository except for average rating*/ {
            return withContext(Dispatchers.IO) {
                val stats = async { WatchAppFirestore.Users.getUserStats(CURRENT_USER) }
                stats.await()
            }
        }

        suspend fun update(subject: String /*Could be changed to enum*/, newValue: String) {
            when (subject) {
                "username" -> WatchAppFirestore.Users.Updates.updateUsername(CURRENT_USER, newValue)
                "gender" -> WatchAppFirestore.Users.Updates.updateGender(CURRENT_USER, newValue)
                "birthYear" -> WatchAppFirestore.Users.Updates.updateBirthYear(CURRENT_USER, newValue.toInt())
                "email" -> WatchAppFirestore.Users.Updates.updateEmail(CURRENT_USER, newValue)
                else -> throw Exception("Illegal argument: $subject")
            }
        }
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
                val potentialUserRating = async { WatchAppFirestore.Ratings.getMediaRating(CURRENT_USER, movieId, true) }
                val containingLists = async { WatchAppFirestore.Lists.getListsContainingMedia(CURRENT_USER, movieId, true) }
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
                val potentialUserRating = async { WatchAppFirestore.Ratings.getMediaRating(CURRENT_USER, seriesId, false) }
                val containingLists = async { WatchAppFirestore.Lists.getListsContainingMedia(CURRENT_USER, seriesId, false) }
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

}