package ru.mtsteta.flixnet.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import ru.mtsteta.flixnet.BuildConfig

val contentType = "application/type".toMediaType()

private val json: Json by lazy {
    Json { ignoreUnknownKeys = true }
}

@ExperimentalSerializationApi
val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BuildConfig.BASE_URL)
    .addConverterFactory(json.asConverterFactory(contentType))
    .build()

interface MovieNetworkAPI {
    @GET("movie/popular")
    suspend fun getPopMovieList(@QueryMap options: Map<String, String>): Response<PopMoviesResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieCrew(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String
    ): Response<MovieCastList>

    @GET("movie/{movie_id}")
    suspend fun getMovieDistributionInfo(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String,
    ): Response<MovieDistributionInfo>

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ru-RU"
    ): Response<GenresResponse>
}

object MovieRemoteService {
    @ExperimentalSerializationApi
    val retrofitService: MovieNetworkAPI by lazy {
        retrofit.create(MovieNetworkAPI::class.java)
    }
}