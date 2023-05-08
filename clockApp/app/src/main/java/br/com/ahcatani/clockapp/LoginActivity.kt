package br.com.ahcatani.clockapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.ahcatani.clockapp.databinding.ActivityLoginBinding
import br.com.ahcatani.clockapp.R

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private fun updateLoginTheme(isWhite: Boolean) {
        val background = if (isWhite) R.color.white else R.color.dark_gray
        val inputFields = if (isWhite) R.color.dark_gray else R.color.light_gray
        val loginButton = if (isWhite) R.color.black else R.color.white
        val logo = if (isWhite) R.drawable.logo_black else R.drawable.logo_white

        binding.loginLayout.setBackgroundColor(ContextCompat.getColor(this, background))
        binding.usernameEditText.backgroundTintList = ContextCompat.getColorStateList(this, inputFields)
        binding.passwordEditText.backgroundTintList = ContextCompat.getColorStateList(this, inputFields)
        binding.loginButton.backgroundTintList = ContextCompat.getColorStateList(this, loginButton)
        binding.logoImageView.setImageResource(logo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val isWhite = sharedPref.getBoolean("isWhite", false)
        binding.switch1.isChecked = isWhite
        updateLoginTheme(isWhite)

        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            updateLoginTheme(isChecked)
            with(sharedPref.edit()) {
                putBoolean("isWhite", isChecked)
                apply()
            }
        }

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (username == "usuario123" && password == "12345") {
                with(sharedPref.edit()) {
                    putBoolean("isLoggedIn", true)
                    apply()
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                binding.errorMessage.apply {
                    text = "Nome de usuário ou senha inválido."
                    setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.red))
                }
            }
        }
    }
}
