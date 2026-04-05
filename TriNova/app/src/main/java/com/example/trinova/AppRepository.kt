package com.example.trinova

class AppRepository {


    suspend fun fetchTriviaQuestions(): List<TriviaQuestion> {
        val response = RetrofitClient.api.getTriviaQuestions()
        return response.results.map { TriviaQuestion.fromApiResult(it) }
    }


    suspend fun fetchSadDog(): String {
        val response = RetrofitClient.api.getRandomDog()
        return response.message
    }


    suspend fun fetchDadJoke(): String {
        val response = RetrofitClient.api.getRandomJoke()
        return response.joke
    }


    suspend fun fetchCatImage(rating: Int): String {
        val categoryId = when (rating) {
            1, 2 -> 5
            3, 4 -> 14
            5, 6 -> 1
            7, 8 -> 4
            9, 10 -> 15
            else -> null
        }

        return try {
            val response = RetrofitClient.api.getCatImage(categoryIds = categoryId)
            if (response.isNotEmpty()) {
                response[0].url
            } else {
                // Fallback: fetch without category filter
                val fallback = RetrofitClient.api.getCatImage(categoryIds = null)
                fallback[0].url
            }
        } catch (e: Exception) {
            // Fallback: fetch without category filter
            val fallback = RetrofitClient.api.getCatImage(categoryIds = null)
            fallback[0].url
        }
    }
}