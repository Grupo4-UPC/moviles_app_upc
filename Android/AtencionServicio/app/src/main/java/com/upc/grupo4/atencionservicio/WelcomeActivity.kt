package com.upc.grupo4.atencionservicio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val tvNombre = findViewById<TextView>(R.id.tvNombre)
        val tvCantidadServicios = findViewById<TextView>(R.id.tvCantidadServicios)
        val btnBuscarItinerario = findViewById<Button>(R.id.btnBuscarItinerario)

        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        tvNombre.text = nombreUsuario

        tvCantidadServicios.text = "1"

        btnBuscarItinerario.setOnClickListener {
            startActivity(Intent(this, ServiceActivity::class.java))
            finish()
        }
    }
}

