package com.jsdr.watchapp.data.repository

import android.util.Log
import com.jsdr.watchapp.data.api.TmdbApiResult
import com.jsdr.watchapp.data.api.TmdbApi
import com.jsdr.watchapp.data.firebase.MovieFirestore
import com.jsdr.watchapp.domain.models.profiles.MovieProfile
import com.jsdr.watchapp.data.models.dtos.movies.MovieDetailsDto
import com.jsdr.watchapp.data.models.dtos.movies.MoviesPageDto
import com.jsdr.watchapp.data.models.dtos.shows.TvSeriesDetailsDto
import com.jsdr.watchapp.data.models.dtos.shows.TvSeriesPageDto
import com.jsdr.watchapp.domain.models.profiles.TvSeriesProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

const val CURRENT_USER: String = "test_user"


object WatchAppRepository {
    const val POSTERS_BASE_URL = "https://image.tmdb.org/t/p/w500"

    suspend fun getMediaProfile(mediaId: Int, mediaType: Boolean /*true - movie, false - tv series*/) {
        if (mediaType) {
            Movies.getMovieProfile(mediaId)
        } else {
            TvSeries.getTvSeriesProfile(mediaId)
        }
    }

    object User {
        suspend fun getUserStats() {
            MovieFirestore.Users.getUserStats(CURRENT_USER)
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
                val potentialUserRating = async { MovieFirestore.Ratings.getMovieRating(CURRENT_USER, movieId.toString()) }
                val containingLists = async { MovieFirestore.Lists.getListsContainingMovie(CURRENT_USER, movieId.toString()) }
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
                val potentialUserRating = async { MovieFirestore.Ratings.getMovieRating(CURRENT_USER, seriesId.toString()) }
                val containingLists = async { MovieFirestore.Lists.getListsContainingMovie(CURRENT_USER, seriesId.toString()) }
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
                        Log.e("Movie Repository", "Could not recieve MoviesPageDto", response.error)
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