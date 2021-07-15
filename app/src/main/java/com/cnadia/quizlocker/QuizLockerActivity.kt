package com.cnadia.quizlocker

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_quiz_locker.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class QuizLockerActivity : AppCompatActivity() {
    var question: JSONObject ?= null

    val answerStatistics by lazy { getSharedPreferences("answerStatistics", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupQuizLocker()

        setContentView(R.layout.activity_quiz_locker)

        showStatistics()

        question = selectQuestion()
        showQuestion(question)

        selectSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when {
                    progress > 95 -> {
                        leftImageView.setImageResource(R.drawable.padlock)
                        rightImageView.setImageResource(R.drawable.unlock)
                    }
                    progress < 5 -> {
                        leftImageView.setImageResource(R.drawable.unlock)
                        rightImageView.setImageResource(R.drawable.padlock)
                    }
                    else -> {
                        leftImageView.setImageResource(R.drawable.padlock)
                        rightImageView.setImageResource(R.drawable.padlock)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress ?: 50
                when {
                    progress > 95 -> {
                        checkChoice(2)
                    }
                    progress < 5 -> {
                        checkChoice(1)
                    }
                    else -> {
                        seekBar?.progress = 50
                    }
                }
            }
        })
    }

    private fun showStatistics() {
        val correct = answerStatistics.getInt("correct", 0)
        val wrong = answerStatistics.getInt("wrong", 0)

        correctCountLabel.text = "정답 횟수 : $correct"
        wrongCountLabel.text = "오답 횟수 : $wrong"
    }

    private fun checkChoice(choice: Int) {
        question?.let {
            when {
                choice == it.getInt("answer") -> { // Correct
                    val correct = answerStatistics.getInt("correct", 0) + 1
                    answerStatistics.edit().putInt("correct", correct).apply()
                    finish()
                }
                else -> { // Wrong
                    val wrong = answerStatistics.getInt("wrong", 0) + 1
                    answerStatistics.edit().putInt("wrong", wrong).apply()

                    selectSeekBar.progress = 50
                    leftImageView.setImageResource(R.drawable.padlock)
                    rightImageView.setImageResource(R.drawable.padlock)
                    errorVibrate()
                }
            }
            showStatistics()
        }
    }

    private fun errorVibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, 100))
        } else {
            vibrator.vibrate(1000)
        }
    }

    private fun showQuestion(question: JSONObject?) {
        quizLabel.text = question?.getString("question")
        choice1.text = question?.getString("choice1")
        choice2.text = question?.getString("choice2")
    }

    private fun selectQuestion(): JSONObject? {
        val jsonText = assets.open("capital.json").reader().readText()
        val questions = JSONArray(jsonText)

        return questions.getJSONObject(Random.nextInt(questions.length()))
    }

    private fun setupQuizLocker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            val keyGuardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            keyGuardManager.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}