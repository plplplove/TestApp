package com.example.testapp

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.databinding.ActivityGameBinding
import com.example.testapp.databinding.DialogGameOverBinding
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private var lives = 3
    private var score = 0
    private var currentIndex = 0
    private var isFirstPairInRound = true
    private var gameTimer: CountDownTimer? = null
    private lateinit var sequence: List<String>

    private val emojis = listOf(
        "😀", "😢", "😐", "😎", "😡", "😍", "🤔",
        "😴", "😱", "😭", "😇", "🤯", "😜", "🤡",
        "🤢", "😷", "🥳", "😈", "👻", "👽", "🤖",
        "🐶", "🐱", "🐵", "🦄", "🐸", "🐼", "🦊",
        "🐰", "🐯", "🐮", "🐷"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startGame()
    }

    private fun startGame() {
        lives = 3
        score = 0
        updateLives()
        updateScore()
        startGameTimer()
        startNewRound()
    }

    private fun startGameTimer() {
        gameTimer?.cancel()
        gameTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                endGame()
            }
        }.start()
    }

    private fun startNewRound() {
        binding.emojiSequence.removeAllViews()
        binding.emojiSequence.visibility = View.VISIBLE
        binding.emojiCards.visibility = View.GONE

        sequence = List(3) { emojis.random() }
        currentIndex = 0
        isFirstPairInRound = true

        sequence.forEach { emoji ->
            val emojiView = createEmojiView(emoji)
            binding.emojiSequence.addView(emojiView)
            animateEmojiEntry(emojiView)
        }

        binding.emojiSequence.postDelayed({
            binding.emojiSequence.visibility = View.GONE
            showNextCardPair()
        }, 2000)
    }

    private fun showNextCardPair() {
        if (currentIndex >= sequence.size) {
            startNewRound()
            return
        }

        binding.emojiCards.visibility = View.VISIBLE

        val correctEmoji = sequence[currentIndex]
        val wrongEmoji = emojis.filter { it != correctEmoji }.random()
        val isCorrectOnLeft = Random.nextBoolean()

        if (isCorrectOnLeft) {
            binding.cardLeft.text = correctEmoji
            binding.cardRight.text = wrongEmoji
        } else {
            binding.cardLeft.text = wrongEmoji
            binding.cardRight.text = correctEmoji
        }

        if (isFirstPairInRound) {
            animateCardEntry()
            isFirstPairInRound = false
        }

        binding.cardLeft.setOnClickListener { handleCardClick(isCorrectOnLeft, binding.cardLeft) }
        binding.cardRight.setOnClickListener { handleCardClick(!isCorrectOnLeft, binding.cardRight) }
    }

    private fun handleCardClick(correct: Boolean, clickedButton: View) {
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

    private fun endGame() {
        gameTimer?.cancel()

        val sharedPreferences = getSharedPreferences("game_prefs", MODE_PRIVATE)
        val bestScore = sharedPreferences.getInt("best_score", 0)

        if (score > bestScore) {
            sharedPreferences.edit().putInt("best_score", score).apply()
        }

        val dialog = Dialog(this)
        val dialogBinding = DialogGameOverBinding.inflate(layoutInflater)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

        dialogBinding.scoreText.text = "Your Score: $score"

        dialogBinding.btnTryAgain.setOnClickListener {
            dialog.dismiss()
            startGame()
        }

        dialogBinding.btnMainMenu.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, GameMenuActivity::class.java))
            finish()
        }

        dialog.show()
    }

    private fun updateLives() {
        binding.livesText.text = "Lives: $lives"
    }

    private fun updateScore() {
        binding.scoreText.text = "Score: $score"
    }

    private fun createEmojiView(emoji: String): TextView {
        return TextView(this).apply {
            text = emoji
            textSize = 40f
            setPadding(16, 0, 16, 0)
            setTextColor(resources.getColor(R.color.dark_brown, null))
            typeface = resources.getFont(R.font.joystixmonospace)
        }
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
        binding.cardLeft.translationX = -500f
        binding.cardLeft.alpha = 0f
        binding.cardLeft.animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(400)
            .start()

        binding.cardRight.translationX = 500f
        binding.cardRight.alpha = 0f
        binding.cardRight.animate()
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

    override fun onDestroy() {
        super.onDestroy()
        gameTimer?.cancel()
    }
}