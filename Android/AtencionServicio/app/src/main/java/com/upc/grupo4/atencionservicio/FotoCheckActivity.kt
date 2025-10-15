package com.upc.grupo4.atencionservicio

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FotoCheckActivity : AppCompatActivity() {

    private lateinit var tvHora: TextView
    private lateinit var tvFecha: TextView
    private lateinit var ivFotoTecnico: ImageView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateTimeRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto_check)

        // Configurar la toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Fotocheck Técnico"

        // Inicializar vistas
        tvHora = findViewById(R.id.tvHora)
        tvFecha = findViewById(R.id.tvFecha)
        ivFotoTecnico = findViewById(R.id.ivFotoTecnico)

        // Cargar la foto de perfil guardada
        loadProfileImage()

        // Actualizar la fecha una vez (no cambia)
        updateFecha()

        // Crear el Runnable para actualizar la hora
        updateTimeRunnable = object : Runnable {
            override fun run() {
                updateHora()
                handler.postDelayed(this, 1000) // Actualizar cada segundo
            }
        }

        // Iniciar la actualización de la hora
        handler.post(updateTimeRunnable)
    }

    private fun loadProfileImage() {
        val prefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        val imagePath = prefs.getString("profile_image_path", null)

        if (imagePath != null) {
            val file = File(imagePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                ivFotoTecnico.setImageBitmap(bitmap)
            } else {
                // Si no existe el archivo, usar la imagen por defecto
                ivFotoTecnico.setImageResource(R.drawable.foto_perfil)
            }
        } else {
            // Si no hay foto guardada, usar la imagen por defecto
            ivFotoTecnico.setImageResource(R.drawable.foto_perfil)
        }
    }

    private fun updateHora() {
        val horaFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        horaFormat.timeZone = TimeZone.getTimeZone("America/Lima")
        val horaActual = horaFormat.format(Date())
        tvHora.text = horaActual
    }

    private fun updateFecha() {
        val fechaFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        fechaFormat.timeZone = TimeZone.getTimeZone("America/Lima")
        val fechaActual = fechaFormat.format(Date())
        tvFecha.text = fechaActual
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener la actualización cuando se cierre la actividad
        handler.removeCallbacks(updateTimeRunnable)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}