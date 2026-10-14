package com.jsdr.watchapp.data.api

import com.jsdr.watchapp.data.models.dtos.movies.MovieDetailsDto
import com.jsdr.watchapp.data.models.dtos.movies.MoviesPageDto
import com.jsdr.watchapp.data.models.dtos.shared.MultiPageDto
import com.jsdr.watchapp.data.models.dtos.shows.TvSeriesDetailsDto
import com.jsdr.watchapp.data.models.dtos.shows.TvSeriesPageDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ITmdbEndpoints {
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false,
        @Query("append_to_response") appendToResponse: String = "credits"
    ): MovieDetailsDto

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false
    ): MoviesPageDto

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false
    ): MoviesPageDto

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false
    ): MoviesPageDto

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String = "en-US",
        @Query("include_adult") includeAdultContent: Boolean = false,
        @Query("region") region: String? = null
    ): MoviesPageDto

    @GET("tv/{series_id}")
    suspend fun getTvSeriesDetails(
        @Path("series_id") seriesId: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false,
        @Query("append_to_response") appendToResponse: String = "credits"
    ): TvSeriesDetailsDto

    @GET("tv/popular")
    suspend fun getPopularTvSeries(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false
    ): TvSeriesPageDto

    @GET("tv/on_the_air")
    suspend fun getOnAirTvSeries(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false
    ): TvSeriesPageDto

    @GET("tv/top_rated")
    suspend fun getTopRatedTvSeries(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false
    ): TvSeriesPageDto

    // endpoints for searching
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") queryInput: String,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false
    ): MoviesPageDto

    @GET("search/tv")
    suspend fun searchTvSeries(
        @Query("query") queryInput: String,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false
    ): TvSeriesPageDto

    @GET("search/multi")
    suspend fun searchMulti(
        @Query("query") queryInput: String,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false
    ): MultiPageDto
}