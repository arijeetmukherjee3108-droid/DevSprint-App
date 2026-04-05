package com.example.trinova

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repository = AppRepository()

    private val _quizState = MutableStateFlow<QuizState>(QuizState.Loading)
    val quizState: StateFlow<QuizState> = _quizState

    private var questions: List<TriviaQuestion> = emptyList()
    private var currentIndex = 0
    private var score = 0
    private var streak = 0 // consecutive correct answers

    init {
        loadQuiz()
    }

    fun loadQuiz() {
        _quizState.value = QuizState.Loading
        currentIndex = 0
        score = 0
        streak = 0
        viewModelScope.launch {
            try {
                questions = repository.fetchTriviaQuestions()
                showCurrentQuestion()
            } catch (e: Exception) {
                _quizState.value = QuizState.Error(
                    "Bruh, even the internet gave up on you.\nTry again maybe? 🤷\n\n${e.message}"
                )
            }
        }
    }

    fun onAnswerSelected(selectedAnswer: String) {
        val currentQuestion = questions.getOrNull(currentIndex) ?: return

        viewModelScope.launch {
            if (selectedAnswer == currentQuestion.correctAnswer) {
                score++
                streak++
                try {
                    val joke = repository.fetchDadJoke()
                    val (hype, emoji) = getHypeMessage()
                    _quizState.value = QuizState.CorrectAnswer(
                        joke = joke,
                        hypeMessage = hype,
                        hypeEmoji = emoji,
                        questionIndex = currentIndex,
                        score = score,
                        totalQuestions = questions.size
                    )
                } catch (e: Exception) {
                    _quizState.value = QuizState.CorrectAnswer(
                        joke = "The joke server is down but YOUR BRAIN ISN'T! 🧠🔥",
                        hypeMessage = "Big brain energy!",
                        hypeEmoji = "🧠",
                        questionIndex = currentIndex,
                        score = score,
                        totalQuestions = questions.size
                    )
                }
            } else {
                streak = 0
                try {
                    val dogUrl = repository.fetchSadDog()
                    val (roast, emoji) = getRoastMessage()
                    _quizState.value = QuizState.WrongAnswer(
                        dogImageUrl = dogUrl,
                        correctAnswer = currentQuestion.correctAnswer,
                        roastMessage = roast,
                        roastEmoji = emoji,
                        questionIndex = currentIndex,
                        score = score,
                        totalQuestions = questions.size
                    )
                } catch (e: Exception) {
                    _quizState.value = QuizState.WrongAnswer(
                        dogImageUrl = "",
                        correctAnswer = currentQuestion.correctAnswer,
                        roastMessage = "So wrong, even the sad dog refused to load for you.",
                        roastEmoji = "💀",
                        questionIndex = currentIndex,
                        score = score,
                        totalQuestions = questions.size
                    )
                }
            }
            // NO DELAY — user taps to continue
        }
    }

    private fun advanceToNext() {
        currentIndex++
        if (currentIndex < questions.size) {
            showCurrentQuestion()
        } else {
            _quizState.value = QuizState.RatingScreen(
                score = score,
                totalQuestions = questions.size
            )
        }
    }

    private fun showCurrentQuestion() {
        val question = questions[currentIndex]
        _quizState.value = QuizState.Playing(
            questionIndex = currentIndex,
            question = question,
            score = score,
            totalQuestions = questions.size
        )
    }

    fun skipFeedback() {
        advanceToNext()
    }

    fun onRatingSubmitted(rating: Int) {
        _quizState.value = QuizState.Loading
        viewModelScope.launch {
            try {
                val catUrl = repository.fetchCatImage(rating)
                val (emoji, message) = getMoodForRating(rating)
                _quizState.value = QuizState.CatResult(
                    catImageUrl = catUrl,
                    rating = rating,
                    moodEmoji = emoji,
                    moodMessage = message,
                    score = score,
                    totalQuestions = questions.size
                )
            } catch (e: Exception) {
                _quizState.value = QuizState.Error(
                    "The cat ran away. Literally. Server said no. 🐱💨"
                )
            }
        }
    }

    // ─── ROAST MESSAGES (wrong answer) ────────────────────────
    private fun getRoastMessage(): Pair<String, String> {
        val roasts = listOf(
            "Bro really said \"let me pick the wrongest answer\" 💀" to "💀",
            "Even a goldfish would've guessed better." to "🐟",
            "Your brain took a vacation and forgot to tell you." to "🏖️",
            "Wrong! This dog is judging you harder than your parents." to "👀",
            "You picked THAT?! With your whole chest?!" to "😭",
            "Tell me you guessed without telling me you guessed." to "🤡",
            "That answer was so wrong, it filed a restraining order." to "📄",
            "Not even close. Like, not even in the same galaxy." to "🌌",
            "The dog in this pic is smarter than you rn." to "🐕",
            "I'd say nice try but... was it really?" to "😬",
            "Bruh. My grandma would've gotten this right. She's 97." to "👵",
            "Your confidence was there. Your knowledge? Not so much." to "📉",
            "Plot twist: every option was wrong except the one you didn't pick." to "🔄",
            "This is giving 'I didn't study but I'll wing it' energy." to "✈️",
            "The answer was right there and you chose violence instead." to "⚔️"
        )
        return roasts.random()
    }

    // ─── HYPE MESSAGES (correct answer) ───────────────────────
    private fun getHypeMessage(): Pair<String, String> {
        val baseHypes = listOf(
            "YOOO you actually knew that?! RESPECT." to "🔥",
            "Big brain moment right there!" to "🧠",
            "Okay okay we see you, smartypants!" to "😎",
            "The prophecy was true... you ARE the chosen one." to "✨",
            "Someone's been reading encyclopedias for fun!" to "📚",
            "You COOKED that question. Medium rare. Perfect." to "👨‍🍳",
            "Give this person a PhD immediately." to "🎓",
            "Not gonna lie, I didn't think you had it in you." to "😏",
            "The neurons are FIRING today!" to "⚡",
            "Your brain said: 'I got this, sit down.'" to "💪"
        )

        val streakHypes = when {
            streak >= 5 -> listOf(
                "5+ IN A ROW?! Are you CHEATING?! (jk keep going 🔥)" to "🏆",
                "UNSTOPPABLE! Someone call the trivia police!" to "🚨",
                "You're on a HEATER! The quiz is crying rn!" to "😤"
            )
            streak >= 3 -> listOf(
                "HAT TRICK! Three in a row! 🎩" to "🎩",
                "You're on FIRE! Do you need a fire extinguisher?!" to "🧯",
                "Three streak! Your brain is in TURBO MODE!" to "🏎️"
            )
            else -> emptyList()
        }

        return if (streakHypes.isNotEmpty() && Math.random() > 0.4) {
            streakHypes.random()
        } else {
            baseHypes.random()
        }
    }

    // ─── CAT MOOD MESSAGES (rating screen) ────────────────────
    private fun getMoodForRating(rating: Int): Pair<String, String> {
        return when (rating) {
            1 -> "😾" to "ONE?! This cat wants to scratch your phone screen."
            2 -> "😾" to "Wow, a 2. This cat has filed an emotional damage lawsuit."
            3 -> "😿" to "This cat just looked at your rating and started crying."
            4 -> "😿" to "A 4?? This cat thought you two were friends..."
            5 -> "🐱" to "Mid rating for a mid quiz? Fair enough, says this cat."
            6 -> "🐱" to "A solid 6. This cat respects your honesty."
            7 -> "😺" to "A 7! This cat is wagging its tail— wait, cats don't do that."
            8 -> "😺" to "EIGHT! This cat is doing the happy paw thing! 🐾"
            9 -> "😻" to "A NINE?! This cat is literally purring at your screen rn."
            10 -> "😻" to "A PERFECT 10?! This cat wants to MARRY you. 💍🐱"
            else -> "🐱" to "This cat is confused but appreciates the attempt."
        }
    }
}