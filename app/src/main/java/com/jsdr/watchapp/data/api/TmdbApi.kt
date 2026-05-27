package com.jsdr.watchapp.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.jsdr.watchapp.data.models.dtos.movies.MovieDetailsDto
import com.jsdr.watchapp.data.models.dtos.movies.MoviesPageDto
import com.jsdr.watchapp.data.models.dtos.shows.TvSeriesDetailsDto
import com.jsdr.watchapp.data.models.dtos.shows.TvSeriesPageDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object TmdbApi {
    private const val API_KEY = "7a0cf0cb349b8912480426231b4faf51"
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private const val LANG_EN = "en-US"
    private const val LANG_PL = "pl-PL"

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(jsonParser.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ITmdbEndpoints::class.java)


    object MoviesData {
        suspend fun fetchMovieDetails(movieId: Int): TmdbApiResult<MovieDetailsDto> {
            return withContext(Dispatchers.IO) {
                try {
                    val movieDetailsDto = api.getMovieDetails(movieId, API_KEY, LANG_PL)
                    TmdbApiResult.OnSuccess(movieDetailsDto)
                } catch (e: Throwable) {
                    TmdbApiResult.OnFailure(e)
                }
            }
        }

        suspend fun fetchMoviesPage(listType: String /*could be changed to enum later*/, pageNumber: Int) : TmdbApiResult<MoviesPageDto> {
            return withContext(Dispatchers.IO) {
                try {
                    val moviesPageDto = when (listType) {
                        "popular" -> api.getPopularMovies(pageNumber, API_KEY, LANG_PL)
                        "top_rated" -> api.getTopRatedMovies(pageNumber, API_KEY, LANG_PL)
                        "upcoming" -> api.getUpcomingMovies(pageNumber, API_KEY, LANG_PL)
                        "now_playing" -> api.getNowPlayingMovies(pageNumber, API_KEY, LANG_PL)
                        else -> throw IllegalArgumentException("Invalid list type: $listType")
                    }
                    TmdbApiResult.OnSuccess(moviesPageDto)
                } catch (e: Throwable) {
                    TmdbApiResult.OnFailure(e)
                }
            }
        }

        suspend fun fetchSearchedMovies(queryInput: String, pageNumber: Int) : TmdbApiResult<MoviesPageDto> {
            return withContext(Dispatchers.IO) {
                try {
                    val moviesPageDto = api.searchMovies(queryInput, pageNumber, API_KEY, LANG_PL)
                    TmdbApiResult.OnSuccess(moviesPageDto)
                } catch (e: Throwable) {
                    TmdbApiResult.OnFailure(e)
                }
            }
        }
    }

    object TvSeriesData {
        suspend fun fetchTvSeriesDetails(seriesId: Int): TmdbApiResult<TvSeriesDetailsDto> {
            return withContext(Dispatchers.IO) {
                try {
                    val seriesDetailsDto = api.getTvSeriesDetails(seriesId, API_KEY, LANG_PL)
                    TmdbApiResult.OnSuccess(seriesDetailsDto)
                } catch (e: Throwable) {
                    TmdbApiResult.OnFailure(e)
                }
            }
        }

        suspend fun fetchTvSeriesPage(listType: String /*change to enum in future*/, pageNumber: Int) : TmdbApiResult<TvSeriesPageDto> {
            return withContext(Dispatchers.IO) {
                try {
                    val tvSeriesPageDto = when (listType) {
                        "popular" -> api.getPopularTvSeries(pageNumber, API_KEY, LANG_PL)
                        "top_rated" -> api.getTopRatedTvSeries(pageNumber, API_KEY, LANG_PL)
                        "on_air" -> api.getOnAirTvSeries(pageNumber, API_KEY, LANG_PL)
                        else -> throw IllegalArgumentException("Invalid list type: $listType")
                    }
                    TmdbApiResult.OnSuccess(tvSeriesPageDto)
                } catch (e: Throwable) {
                    TmdbApiResult.OnFailure(e)
                }
            }
        }

        suspend fun fetchSearchedTvSeries(queryInput: String, pageNumber: Int) : TmdbApiResult<TvSeriesPageDto> {
            return withContext(Dispatchers.IO) {
                try {
                    val tvSeriesPageDto = api.searchTvSeries(queryInput, pageNumber, API_KEY, LANG_PL)
                    TmdbApiResult.OnSuccess(tvSeriesPageDto)
                } catch (e: Throwable) {
                    TmdbApiResult.OnFailure(e)
                }
            }
        }
    }

    suspend fun mapToGeneralOverview() {
        // needs moving to repository
    }
}