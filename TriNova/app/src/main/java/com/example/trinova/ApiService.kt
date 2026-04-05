package com.example.trinova

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {


    @GET("https://opentdb.com/api.php?amount=10&type=multiple")
    suspend fun getTriviaQuestions(): TriviaApiResponse

    @GET("https://dog.ceo/api/breeds/image/random")
    suspend fun getRandomDog(): DogResponse

    @Headers("Accept: application/json")
    @GET("https://icanhazdadjoke.com/")
    suspend fun getRandomJoke(): JokeResponse

    @GET("https://api.thecatapi.com/v1/images/search")
    suspend fun getCatImage(
        @Query("limit") limit: Int = 1,
        @Query("category_ids") categoryIds: Int? = null
    ): List<CatImageResponse>
}