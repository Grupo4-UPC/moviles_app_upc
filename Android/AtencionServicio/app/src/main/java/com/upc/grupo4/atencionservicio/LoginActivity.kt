package com.upc.grupo4.atencionservicio

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.upc.grupo4.atencionservicio.databinding.ActivityLoginBinding
import org.json.JSONObject

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

            val url = "http://10.0.2.2:3000/api/v1/solicitudes-tributarias/auth"
            val params = JSONObject().apply {
                put("user", email)
                put("password", password)
            }

            val request = JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                { response ->
                    try {
                        val success = response.getBoolean("success")
                        if (success) {
                            val data = response.getJSONObject("data")
                            val token = data.getString("token")

                            val userInfo = decodeJwtPayload(token)


                            val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                            prefs.edit()
                                .putString("jwt", token)
                                .putInt("id", userInfo.optInt("sub", -1))
                                .putString("usuario", userInfo.optString("usuario", ""))
                                .putString("nombre", userInfo.optString("nombre", ""))
                                .putString("role", userInfo.optString("role", ""))
                                .putString("menu", userInfo.optJSONArray("menu")?.toString())
                                .apply()

                            Toast.makeText(this, "Bienvenido ${userInfo.optString("nombre")} ✅", Toast.LENGTH_SHORT).show()

                            startActivity(Intent(this, OnboardingActivity::class.java))
                            finish()
                        } else {
                            val message = response.optString("message", "Usuario o contraseña incorrectos")
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

            Volley.newRequestQueue(this).add(request)
        }

        binding.btnForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun decodeJwtPayload(token: String): JSONObject {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return JSONObject()
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            JSONObject(payload)
        } catch (e: Exception) {
            JSONObject()
        }
    }
}
