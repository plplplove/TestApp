package com.example.testapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.databinding.ActivityGameMenuBinding
import com.example.testapp.databinding.DialogGameRulesBinding

class GameMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameMenuBinding
    private lateinit var bestScoreText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bestScoreText = binding.bestScoreText
        updateBestScoreDisplay()

        setupButton(binding.btnStartGame) {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            finish()
        }
        setupButton(binding.btnGameRules) {
            showGameRulesDialog()
        }
        setupButton(binding.btnExitGame) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        updateBestScoreDisplay()
    }

    private fun updateBestScoreDisplay() {
        val sharedPreferences = getSharedPreferences("game_prefs", MODE_PRIVATE)
        val bestScore = sharedPreferences.getInt("best_score", 0)
        bestScoreText.text = getString(R.string.best_score_format, bestScore)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupButton(button: Button, onClick: () -> Unit) {
        button.setOnTouchListener { view, event -> handleTouch(view, event) }
        button.setOnClickListener { onClick() }
    }

    private fun handleTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                view.scaleX = 0.95f
                view.scaleY = 0.95f
                view.alpha = 0.8f
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                view.scaleX = 1f
                view.scaleY = 1f
                view.alpha = 1f
                if (event.action == MotionEvent.ACTION_UP) {
                    view.performClick()
                }
            }
        }
        return true
    }

    private fun showGameRulesDialog() {
        val dialog = Dialog(this)
        val bindingDialog = DialogGameRulesBinding.inflate(layoutInflater)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingDialog.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

        setupButton(bindingDialog.btnOk) {
            dialog.dismiss()
        }

        dialog.show()
    }
}