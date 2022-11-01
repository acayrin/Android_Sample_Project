package me.acayrin.sampleproject.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.database.dao.DAOLibrarian
import me.acayrin.sampleproject.database.model.Librarian

class SetupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        findViewById<Button>(R.id.register_submit).setOnClickListener {
            val etUsername = findViewById<EditText>(R.id.register_username)
            val etPassphrase = findViewById<EditText>(R.id.register_password)

            getSharedPreferences("__secret", MODE_PRIVATE)
                .edit()
                .putString("__username", etUsername.text.toString())
                .apply()

            if (DAOLibrarian(this).insert(
                    Librarian(
                            0,
                            null,
                            null,
                            etUsername.text.toString(),
                            etPassphrase.text.toString()
                        )
                )
            ) {
                getSharedPreferences("currentUser", MODE_PRIVATE)
                    .edit()
                    .putInt("userId", 0)
                    .apply()

                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }
}
