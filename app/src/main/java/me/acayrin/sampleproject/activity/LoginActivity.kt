package me.acayrin.sampleproject.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.database.dao.DAOLibrarian

class LoginActivity : AppCompatActivity() {
    private lateinit var userDAO: DAOLibrarian

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userDAO = DAOLibrarian(this)
        if (userDAO.all.size == 0) {
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.login_username)
        val etPassword = findViewById<EditText>(R.id.login_password)

        findViewById<Button>(R.id.login_submit).setOnClickListener {
            var hasError = false

            if (etUsername.text.toString().isEmpty()) {
                hasError = true
                etUsername.error = "This field cannot be empty"
            } else {
                etUsername.error = null
            }

            if (etPassword.text.toString().isEmpty()) {
                hasError = true
                etPassword.error = "This field cannot be empty"
            } else {
                etPassword.error = null
            }

            if (hasError) return@setOnClickListener

            val response =
                userDAO.validate(etUsername.text.toString(), etPassword.text.toString())
            if (response != null) {
                val sharedPreferences = getSharedPreferences("currentUser", MODE_PRIVATE)
                sharedPreferences
                    .edit()
                    .putInt("userId", response.id)
                    .putString("username", response.username)
                    .apply()

                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                etUsername.error = "Incorrect username or password"
            }
        }
    }
}
