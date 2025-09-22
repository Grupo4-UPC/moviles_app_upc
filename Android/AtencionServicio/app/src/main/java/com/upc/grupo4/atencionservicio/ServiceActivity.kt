package com.upc.grupo4.atencionservicio

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
data class Service(
    val id: String,
    val clientName: String,
    val address: String,
    val shift: String,
    val sku: String
)
class ServiceActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var serviceListContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_service)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbar = findViewById(R.id.toolbarService)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Atencion de Servicio"


        serviceListContainer = findViewById<LinearLayout>(R.id.serviceListContainer)

        val services = listOf(
            Service("354140-2", "Juan Pérez", "Av. Siempre Viva 123", "Mañana", "001234"),
            Service("354140-3", "Maria Gómez", "Calle Ficticia 456", "Tarde", "001235"),
            Service("354140-4", "Carlos López", "Av. La Paz 789", "Noche", "001236")
        )
        services.forEach { service ->
            val itemView = layoutInflater.inflate(R.layout.activity_item_service, serviceListContainer, false)

            val tvServiceID: TextView = itemView.findViewById(R.id.tvServiceID)
            val tvClientName: TextView = itemView.findViewById(R.id.tvClientName)
            val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
            val tvShift: TextView = itemView.findViewById(R.id.tvShift)
            val tvSKU: TextView = itemView.findViewById(R.id.tvSKU)
            val btnStartService: Button = itemView.findViewById(R.id.btnStartService)

            tvServiceID.text = "OS - ${service.id}"
            tvClientName.text = "Cliente: ${service.clientName}"
            tvAddress.text = "Dirección: ${service.address}"
            tvShift.text = "Turno: ${service.shift}"
            tvSKU.text = "SKU: ${service.sku}"

            btnStartService.setOnClickListener {
                val intent = Intent(this, StartServiceActivity::class.java)
                startActivity(intent)
            }

            serviceListContainer.addView(itemView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_faq, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_home -> {
                startActivity(Intent(this, LoginActivity::class.java))
                true
            }
            R.id.menu_faq -> {
                startActivity(Intent(this, FAQActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}