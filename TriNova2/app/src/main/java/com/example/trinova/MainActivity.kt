package com.example.trinova

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()


    private lateinit var loadingContainer: LinearLayout
    private lateinit var quizContainer: ScrollView
    private lateinit var wrongAnswerContainer: LinearLayout
    private lateinit var correctAnswerContainer: LinearLayout
    private lateinit var ratingContainer: ScrollView
    private lateinit var catResultContainer: ScrollView
    private lateinit var errorContainer: LinearLayout


    private lateinit var tvQuestionProgress: TextView
    private lateinit var tvScore: TextView
    private lateinit var quizProgressBar: ProgressBar
    private lateinit var tvCategory: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var btnOption1: MaterialButton
    private lateinit var btnOption2: MaterialButton
    private lateinit var btnOption3: MaterialButton
    private lateinit var btnOption4: MaterialButton

    // Wrong answer views
    private lateinit var tvRoastEmoji: TextView
    private lateinit var tvRoastMessage: TextView
    private lateinit var tvCorrectAnswer: TextView
    private lateinit var ivSadDog: ImageView

    // Correct answer views
    private lateinit var tvHypeEmoji: TextView
    private lateinit var tvHypeMessage: TextView
    private lateinit var tvDadJoke: TextView

    // Rating views
    private lateinit var tvFinalScore: TextView
    private lateinit var tvRatingValue: TextView
    private lateinit var tvRatingEmoji: TextView
    private lateinit var ratingSlider: Slider
    private lateinit var btnSubmitRating: MaterialButton

    // Cat result views
    private lateinit var tvCatMoodEmoji: TextView
    private lateinit var tvCatMoodMessage: TextView
    private lateinit var ivCatImage: ImageView
    private lateinit var tvCatRatingInfo: TextView
    private lateinit var btnPlayAgain: MaterialButton

    // Error views
    private lateinit var tvError: TextView
    private lateinit var btnRetry: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        setupListeners()
        observeState()
    }

    private fun bindViews() {
        // Containers
        loadingContainer = findViewById(R.id.loadingContainer)
        quizContainer = findViewById(R.id.quizContainer)
        wrongAnswerContainer = findViewById(R.id.wrongAnswerContainer)
        correctAnswerContainer = findViewById(R.id.correctAnswerContainer)
        ratingContainer = findViewById(R.id.ratingContainer)
        catResultContainer = findViewById(R.id.catResultContainer)
        errorContainer = findViewById(R.id.errorContainer)

        // Quiz
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress)
        tvScore = findViewById(R.id.tvScore)
        quizProgressBar = findViewById(R.id.quizProgressBar)
        tvCategory = findViewById(R.id.tvCategory)
        tvQuestion = findViewById(R.id.tvQuestion)
        btnOption1 = findViewById(R.id.btnOption1)
        btnOption2 = findViewById(R.id.btnOption2)
        btnOption3 = findViewById(R.id.btnOption3)
        btnOption4 = findViewById(R.id.btnOption4)

        // Wrong answer
        tvRoastEmoji = findViewById(R.id.tvRoastEmoji)
        tvRoastMessage = findViewById(R.id.tvRoastMessage)
        tvCorrectAnswer = findViewById(R.id.tvCorrectAnswer)
        ivSadDog = findViewById(R.id.ivSadDog)

        // Correct answer
        tvHypeEmoji = findViewById(R.id.tvHypeEmoji)
        tvHypeMessage = findViewById(R.id.tvHypeMessage)
        tvDadJoke = findViewById(R.id.tvDadJoke)

        // Rating
        tvFinalScore = findViewById(R.id.tvFinalScore)
        tvRatingValue = findViewById(R.id.tvRatingValue)
        tvRatingEmoji = findViewById(R.id.tvRatingEmoji)
        ratingSlider = findViewById(R.id.ratingSlider)
        btnSubmitRating = findViewById(R.id.btnSubmitRating)

        // Cat result
        tvCatMoodEmoji = findViewById(R.id.tvCatMoodEmoji)
        tvCatMoodMessage = findViewById(R.id.tvCatMoodMessage)
        ivCatImage = findViewById(R.id.ivCatImage)
        tvCatRatingInfo = findViewById(R.id.tvCatRatingInfo)
        btnPlayAgain = findViewById(R.id.btnPlayAgain)

        // Error
        tvError = findViewById(R.id.tvError)
        btnRetry = findViewById(R.id.btnRetry)
    }

    private fun setupListeners() {
        // Option buttons → send selected answer to ViewModel
        val optionButtons = listOf(btnOption1, btnOption2, btnOption3, btnOption4)
        optionButtons.forEach { button ->
            button.setOnClickListener {
                // Disable all buttons to prevent double-tap
                optionButtons.forEach { it.isEnabled = false }
                viewModel.onAnswerSelected(button.text.toString())
            }
        }

        // Feedback overlays → tap to continue (NO DELAY!)
        wrongAnswerContainer.setOnClickListener {
            viewModel.skipFeedback()
        }
        correctAnswerContainer.setOnClickListener {
            viewModel.skipFeedback()
        }

        // Rating slider with live emoji + funny label updates
        ratingSlider.addOnChangeListener { _, value, _ ->
            val rating = value.toInt()
            tvRatingValue.text = rating.toString()
            tvRatingEmoji.text = when (rating) {
                1 -> "😾"
                2 -> "😾"
                3 -> "😿"
                4 -> "😿"
                5 -> "🐱"
                6 -> "🐱"
                7 -> "😺"
                8 -> "😺"
                9 -> "😻"
                10 -> "😻"
                else -> "🐱"
            }
        }

        // Submit rating
        btnSubmitRating.setOnClickListener {
            viewModel.onRatingSubmitted(ratingSlider.value.toInt())
        }

        // Play again
        btnPlayAgain.setOnClickListener {
            viewModel.loadQuiz()
        }

        // Retry on error
        btnRetry.setOnClickListener {
            viewModel.loadQuiz()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.quizState.collect { state ->
                // Hide all containers first
                hideAll()

                when (state) {
                    is QuizState.Loading -> showLoading()
                    is QuizState.Playing -> showQuestion(state)
                    is QuizState.WrongAnswer -> showWrongAnswer(state)
                    is QuizState.CorrectAnswer -> showCorrectAnswer(state)
                    is QuizState.RatingScreen -> showRating(state)
                    is QuizState.CatResult -> showCatResult(state)
                    is QuizState.Error -> showError(state)
                }
            }
        }
    }

    private fun hideAll() {
        loadingContainer.visibility = View.GONE
        quizContainer.visibility = View.GONE
        wrongAnswerContainer.visibility = View.GONE
        correctAnswerContainer.visibility = View.GONE
        ratingContainer.visibility = View.GONE
        catResultContainer.visibility = View.GONE
        errorContainer.visibility = View.GONE
    }

    private fun showLoading() {
        loadingContainer.visibility = View.VISIBLE
    }

    private fun showQuestion(state: QuizState.Playing) {
        quizContainer.visibility = View.VISIBLE

        tvQuestionProgress.text = "Question ${state.questionIndex + 1}/${state.totalQuestions}"
        tvScore.text = "Score: ${state.score}"
        quizProgressBar.max = state.totalQuestions
        quizProgressBar.progress = state.questionIndex + 1
        tvQuestion.text = state.question.question

        val options = state.question.options
        val buttons = listOf(btnOption1, btnOption2, btnOption3, btnOption4)
        buttons.forEachIndexed { index, button ->
            if (index < options.size) {
                button.text = options[index]
                button.visibility = View.VISIBLE
                button.isEnabled = true
            } else {
                button.visibility = View.GONE
            }
        }

        // Scroll to top
        quizContainer.scrollTo(0, 0)
    }

    private fun showWrongAnswer(state: QuizState.WrongAnswer) {
        wrongAnswerContainer.visibility = View.VISIBLE

        // Set the dynamic roast content
        tvRoastEmoji.text = state.roastEmoji
        tvRoastMessage.text = state.roastMessage
        tvCorrectAnswer.text = "✅ Correct answer: ${state.correctAnswer}"

        if (state.dogImageUrl.isNotEmpty()) {
            ivSadDog.load(state.dogImageUrl) {
                crossfade(true)
                crossfade(300)
            }
        }
    }

    private fun showCorrectAnswer(state: QuizState.CorrectAnswer) {
        correctAnswerContainer.visibility = View.VISIBLE

        // Set the dynamic hype content
        tvHypeEmoji.text = state.hypeEmoji
        tvHypeMessage.text = state.hypeMessage
        tvDadJoke.text = state.joke
    }

    private fun showRating(state: QuizState.RatingScreen) {
        ratingContainer.visibility = View.VISIBLE

        val scoreMsg = when {
            state.score == state.totalQuestions -> "PERFECT SCORE?! ${state.score}/${state.totalQuestions}! Are you Google?! 🤖"
            state.score >= 8 -> "You scored ${state.score}/${state.totalQuestions}! Big brain energy! 🧠"
            state.score >= 5 -> "You scored ${state.score}/${state.totalQuestions}. Not bad! Not great! Average! 📊"
            state.score >= 3 -> "You scored ${state.score}/${state.totalQuestions}... the dogs felt that. 🐕"
            else -> "You scored ${state.score}/${state.totalQuestions}... bruh. 💀"
        }
        tvFinalScore.text = scoreMsg

        ratingSlider.value = 5f
        tvRatingValue.text = "5"
        tvRatingEmoji.text = "🐱"
    }

    private fun showCatResult(state: QuizState.CatResult) {
        catResultContainer.visibility = View.VISIBLE
        tvCatMoodEmoji.text = state.moodEmoji
        tvCatMoodMessage.text = state.moodMessage
        tvCatRatingInfo.text = "Your rating: ${state.rating}/10 • Score: ${state.score}/${state.totalQuestions}"

        ivCatImage.load(state.catImageUrl) {
            crossfade(true)
            crossfade(300)
        }
    }

    private fun showError(state: QuizState.Error) {
        errorContainer.visibility = View.VISIBLE
        tvError.text = state.message
    }
}