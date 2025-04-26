package com.example.testapp

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var livesText: TextView
    private lateinit var scoreText: TextView
    private lateinit var timerText: TextView

    private lateinit var emojiSequenceLayout: LinearLayout
    private lateinit var emojiCardsLayout: LinearLayout

    private lateinit var cardLeft: Button
    private lateinit var cardRight: Button

    private var lives = 3
    private var score = 0

    private val emojis = listOf("😀", "😢", "😐", "😎", "😡", "😍", "🤔",
        "😴", "😱", "😭", "😇", "🤯", "😜", "🤡",
        "🤢", "😷", "🥳", "😈", "👻", "👽", "🤖",
        "🐶", "🐱", "🐵", "🦄", "🐸", "🐼", "🦊",
        "🐰", "🐯", "🐮", "🐷")

    private lateinit var sequence: List<String>
    private var currentIndex = 0

    private var gameTimer: CountDownTimer? = null

    private var isFirstPairInRound = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        livesText = findViewById(R.id.livesText)
        scoreText = findViewById(R.id.scoreText)
        timerText = findViewById(R.id.timerText)

        emojiSequenceLayout = findViewById(R.id.emojiSequence)
        emojiCardsLayout = findViewById(R.id.emojiCards)

        cardLeft = findViewById(R.id.cardLeft)
        cardRight = findViewById(R.id.cardRight)

        startGameTimer()
        startNewRound()
    }

    private fun startGameTimer() {
        gameTimer?.cancel()

        gameTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                timerText.text = "$secondsLeft"
            }

            override fun onFinish() {
                endGame()
            }
        }.start()
    }

    private fun startNewRound() {
        emojiSequenceLayout.removeAllViews()
        emojiSequenceLayout.visibility = View.VISIBLE
        emojiCardsLayout.visibility = View.GONE

        sequence = List(3) { emojis.random() }
        currentIndex = 0
        isFirstPairInRound = true

        for (emoji in sequence) {
            val emojiView = TextView(this).apply {
                text = emoji
                textSize = 40f
                setPadding(16, 0, 16, 0)
                setTextColor(resources.getColor(R.color.dark_brown))
                typeface = resources.getFont(R.font.joystixmonospace)
            }
            emojiSequenceLayout.addView(emojiView)
            animateEmojiEntry(emojiView)
        }

        emojiSequenceLayout.postDelayed({
            emojiSequenceLayout.visibility = View.GONE
            showNextCardPair()
        }, 2000)
    }

    private fun showNextCardPair() {
        if (currentIndex >= sequence.size) {
            startNewRound()
            return
        }

        emojiCardsLayout.visibility = View.VISIBLE

        val correctEmoji = sequence[currentIndex]
        val wrongEmoji = emojis.filter { it != correctEmoji }.random()
        val isCorrectOnLeft = Random.nextBoolean()

        if (isCorrectOnLeft) {
            cardLeft.text = correctEmoji
            cardRight.text = wrongEmoji
        } else {
            cardLeft.text = wrongEmoji
            cardRight.text = correctEmoji
        }

        if (isFirstPairInRound) {
            animateCardEntry()
            isFirstPairInRound = false
        }

        cardLeft.setOnClickListener {
            handleCardClick(isCorrectOnLeft, cardLeft)
        }
        cardRight.setOnClickListener {
            handleCardClick(!isCorrectOnLeft, cardRight)
        }
    }

    private fun handleCardClick(correct: Boolean, clickedButton: Button) {
        animateCardClick(clickedButton)

        if (correct) {
            score++
            updateScore()
        } else {
            lives--
            updateLives()
            animateWrongAnswer(clickedButton)
            if (lives <= 0) {
                endGame()
                return
            }
        }

        currentIndex++
        showNextCardPair()
    }

    private fun animateEmojiEntry(view: View) {
        view.translationY = 300f
        view.alpha = 0f
        view.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(500)
            .start()
    }

    private fun animateCardEntry() {
        cardLeft.translationX = -500f
        cardLeft.alpha = 0f
        cardLeft.animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(400)
            .start()

        cardRight.translationX = 500f
        cardRight.alpha = 0f
        cardRight.animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(400)
            .start()
    }

    private fun animateCardClick(view: View) {
        view.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    private fun animateWrongAnswer(view: View) {
        view.animate()
            .translationXBy(20f)
            .setDuration(50)
            .withEndAction {
                view.animate()
                    .translationXBy(-40f)
                    .setDuration(50)
                    .withEndAction {
                        view.animate()
                            .translationXBy(20f)
                            .setDuration(50)
                            .start()
                    }
                    .start()
            }
            .start()
    }

    private fun updateLives() {
        livesText.text = "Lives: $lives"
    }

    private fun updateScore() {
        scoreText.text = "Score: $score"
    }

    private fun endGame() {
        gameTimer?.cancel()
        // TODO: Перехід на ResultActivity
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameTimer?.cancel()
    }
}