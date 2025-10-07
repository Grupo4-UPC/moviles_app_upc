package com.upc.grupo4.atencionservicio

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.upc.grupo4.atencionservicio.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEnter.setOnClickListener {
            val email = binding.etUser.text?.toString()?.trim().orEmpty()
            val password = binding.etPassword.text?.toString()?.trim().orEmpty()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url = "http://10.0.2.2:3000/api/v1/solicitudes-tributarias/auth" // tu backend
            val params = org.json.JSONObject().apply {
                put("user", email)
                put("password", password)
            }

            val request = com.android.volley.toolbox.JsonObjectRequest(
                com.android.volley.Request.Method.POST,
                url,
                params,
                { response ->
                    try {
                        val success = response.getBoolean("success")
                        if (success) {
                            val data = response.getJSONObject("data")
                            val token = data.getString("token")  // ajusta si tu backend devuelve distinto
                            Toast.makeText(this, "Login exitoso ✅", Toast.LENGTH_SHORT).show()

                            // 👉 Guarda el token (opcional con SharedPreferences)
                            val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                            prefs.edit().putString("jwt", token).apply()

                            // Pasa a la siguiente pantalla
                            startActivity(Intent(this, OnboardingActivity::class.java))
                            finish()
                        } else {
                            val message = response.optString("message", "Error de autenticación")
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                },
                { error ->
                    Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_SHORT).show()
                    error.printStackTrace()
                }
            )

            val queue = com.android.volley.toolbox.Volley.newRequestQueue(this)
            queue.add(request)
        }


        binding.btnForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_faq, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_faq -> {
                startActivity(Intent(this, FAQActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
