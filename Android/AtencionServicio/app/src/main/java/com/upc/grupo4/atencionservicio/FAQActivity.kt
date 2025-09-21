package com.upc.grupo4.atencionservicio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FAQActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        val toolbar: Toolbar = findViewById(R.id.faqToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // flecha volver
        supportActionBar?.title = "FAQ"

        val recyclerView: RecyclerView = findViewById(R.id.rvFAQ)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Datos
        val faqList = listOf(
            "¿Cómo registro mi cuenta?" to "Puedes registrarte desde el botón de 'Registrarse'...",
            "¿Olvidé mi contraseña, qué hago?" to "Usa el botón 'Olvidé mi contraseña'..."
        )

        recyclerView.adapter = FAQAdapter(faqList)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
