package br.com.ahcatani.clockapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.Locale
import kotlin.math.floor
import androidx.core.content.ContextCompat
import android.content.Context
import android.content.Intent
import android.app.Activity
import android.content.SharedPreferences


class MainActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var startButton: Button
    private lateinit var resetButton: Button

    private var isRunning = false
    private var elapsedTime = 0L
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var toggleThemeSwitch: SwitchCompat
    private var isDarkMode = true

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            val minutes = floor(elapsedTime / 60000.0).toLong()
            val seconds = floor((elapsedTime % 60000) / 1000.0).toLong()
            val milliseconds = floor((elapsedTime % 1000) / 10.0).toLong()

            val timeText = String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                minutes,
                seconds,
                milliseconds
            )
            timerTextView.text = timeText
            elapsedTime += 10

            handler.postDelayed(this, 1)
        }
    }

    private fun showLoginActivity() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivityForResult(loginIntent, LOGIN_ACTIVITY_REQUEST_CODE)
    }

    private fun updateTheme() {
        if (isDarkMode) {
            timerTextView.setTextColor(Color.WHITE)
            findViewById<ConstraintLayout>(R.id.rootLayout).setBackgroundColor(Color.BLACK)
            toggleThemeSwitch.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switch_track_dark))
        } else {
            timerTextView.setTextColor(Color.BLACK)
            findViewById<ConstraintLayout>(R.id.rootLayout).setBackgroundColor(Color.WHITE)
            toggleThemeSwitch.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switch_track_light))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        if (!isLoggedIn) {
            showLoginActivity()
        }

        setContentView(R.layout.activity_main)

        timerTextView = findViewById(R.id.timer)
        startButton = findViewById(R.id.startButton)
        resetButton = findViewById(R.id.resetButton)

        startButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#008000"))
        startButton.setTextColor(Color.parseColor("#00FF00"))

        resetButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#808080"))
        resetButton.setTextColor(Color.parseColor("#D3D3D3"))

        startButton.setOnClickListener {
            if (!isRunning) {
                isRunning = true
                startButton.text = "Parar"
                startButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#8B0000"))
                startButton.setTextColor(Color.parseColor("#FF4500"))
                handler.post(updateTimeRunnable)
            } else {
                isRunning = false
                startButton.text = "Iniciar"
                startButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#008000"))
                startButton.setTextColor(Color.parseColor("#00FF00"))
                handler.removeCallbacks(updateTimeRunnable)
            }
        }

        resetButton.setOnClickListener {
            if (!isRunning) {
                elapsedTime = 0
                timerTextView.text = "00:00,00"
            }
        }

        toggleThemeSwitch = findViewById(R.id.toggleThemeSwitch)
        updateTheme()

        toggleThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            isDarkMode = !isChecked
            updateTheme()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isRunning) {
            handler.removeCallbacks(updateTimeRunnable)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isRunning) {
            handler.post(updateTimeRunnable)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOGIN_ACTIVITY_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean("isLoggedIn", false)
                    apply()
                }

                showLoginActivity()
            }
        }
    }

    companion object {
        private const val LOGIN_ACTIVITY_REQUEST_CODE = 1
    }
}

