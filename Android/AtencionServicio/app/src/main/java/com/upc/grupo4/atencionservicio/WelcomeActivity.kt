package com.upc.grupo4.atencionservicio

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.upc.grupo4.atencionservicio.dialogs.InfoDialogFragment
import com.upc.grupo4.atencionservicio.util.LoadingDialog

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val tvNombre = findViewById<TextView>(R.id.tvNombre)
        val tvCantidadServicios = findViewById<TextView>(R.id.tvCantidadServicios)
        val btnBuscarItinerario = findViewById<Button>(R.id.btn_search_itinerary)

        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val nombre = prefs.getString("nombre", "Usuario")
        val usuario = prefs.getString("usuario", "")
        val role = prefs.getString("role", "")
        val menu = prefs.getString("menu", "[]")

        tvNombre.text = nombre
        tvCantidadServicios.text = ""

        btnBuscarItinerario.setOnClickListener {
            LoadingDialog.show(this)

            Handler(Looper.getMainLooper()).postDelayed({
                LoadingDialog.hide()

                startActivity(Intent(this, MenuActivity::class.java))
                finish()
            }, 2000)
        }
    }
}

