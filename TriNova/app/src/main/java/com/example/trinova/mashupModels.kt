package com.example.trinova

import android.text.Html

// ─── Open Trivia DB Response ──────────────────────────────
data class TriviaApiResponse(
    val response_code: Int,
    val results: List<TriviaResult>
)

data class TriviaResult(
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)

// Processed trivia question ready for display
data class TriviaQuestion(
    val question: String,
    val correctAnswer: String,
    val options: List<String> // shuffled: 1 correct + 3 incorrect
) {
    companion object {
        fun fromApiResult(result: TriviaResult): TriviaQuestion {
            val decodedQuestion = Html.fromHtml(result.question, Html.FROM_HTML_MODE_LEGACY).toString()
            val decodedCorrect = Html.fromHtml(result.correct_answer, Html.FROM_HTML_MODE_LEGACY).toString()
            val decodedIncorrect = result.incorrect_answers.map {
                Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY).toString()
            }
            val allOptions = (decodedIncorrect + decodedCorrect).shuffled()
            return TriviaQuestion(
                question = decodedQuestion,
                correctAnswer = decodedCorrect,
                options = allOptions
            )
        }
    }
}

// ─── Dog CEO API Response ─────────────────────────────────
data class DogResponse(val message: String, val status: String)

// ─── icanhazdadjoke Response ──────────────────────────────
data class JokeResponse(val id: String, val joke: String, val status: Int)

// ─── The Cat API Response ─────────────────────────────────
data class CatImageResponse(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int
)

// ─── Quiz UI State (drives the single-screen UI) ─────────
sealed class QuizState {
    object Loading : QuizState()

    data class Playing(
        val questionIndex: Int,
        val question: TriviaQuestion,
        val score: Int,
        val totalQuestions: Int
    ) : QuizState()

    data class WrongAnswer(
        val dogImageUrl: String,
        val correctAnswer: String,
        val roastMessage: String,
        val roastEmoji: String,
        val questionIndex: Int,
        val score: Int,
        val totalQuestions: Int
    ) : QuizState()

    data class CorrectAnswer(
        val joke: String,
        val hypeMessage: String,
        val hypeEmoji: String,
        val questionIndex: Int,
        val score: Int,
        val totalQuestions: Int
    ) : QuizState()

    data class RatingScreen(
        val score: Int,
        val totalQuestions: Int
    ) : QuizState()

    data class CatResult(
        val catImageUrl: String,
        val rating: Int,
        val moodEmoji: String,
        val moodMessage: String,
        val score: Int,
        val totalQuestions: Int
    ) : QuizState()

    data class Error(val message: String) : QuizState()
}
