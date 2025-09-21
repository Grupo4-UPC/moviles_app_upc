package com.upc.grupo4.atencionservicio

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FAQActivity : AppCompatActivity() {

    private lateinit var adapter: FAQAdapter
    private lateinit var faqList: List<Pair<String, String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        //Toolbar
        val toolbar: Toolbar = findViewById(R.id.faqToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // flecha volver
        supportActionBar?.title = "FAQ"

        //RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.rvFAQ)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Datos
        faqList = mutableListOf(
            "¿Cómo registro mi cuenta?" to "Puedes registrarte desde el botón de 'Registrarse' ubicado en la pantalla principal. Solo necesitas un correo válido y una contraseña segura.",
            "¿Olvidé mi contraseña, qué hago?" to "Usa el botón 'Olvidé mi contraseña' y sigue las instrucciones para restablecer tu clave mediante tu correo electrónico registrado.",
            "¿Cómo contacto soporte?" to "Puedes contactarnos por correo a soporte@miapp.com o usar el chat en vivo dentro de la aplicación, disponible 24/7.",
            "¿Dónde veo mis pedidos?" to "Ingresa a 'Mis pedidos' en el menú principal para ver el estado de cada uno de tus pedidos y su historial completo.",
            "¿Puedo cambiar mi correo electrónico?" to "Sí, ve a 'Configuración de cuenta' y selecciona 'Editar correo electrónico'. Recibirás un correo de verificación para confirmar el cambio.",
            "¿Cómo cancelo un pedido?" to "Si tu pedido aún no ha sido procesado, ingresa al detalle del pedido y pulsa 'Cancelar'. Recibirás la confirmación por correo.",
            "¿Puedo programar entregas?" to "Al momento de realizar tu compra, selecciona la opción 'Programar entrega' y elige la fecha y hora disponibles.",
            "¿Cómo aplico un cupón de descuento?" to "En el carrito de compras, introduce el código de descuento en el campo 'Cupón' antes de pagar y se aplicará automáticamente.",
            "¿Qué hago si un producto llega dañado?" to "Contacta al soporte y adjunta fotos del producto. Procederemos con el reemplazo o reembolso según corresponda.",
            "¿Cómo elimino mi cuenta?" to "Ve a 'Configuración de cuenta' > 'Eliminar cuenta'. Ten en cuenta que esta acción es irreversible y perderás todo tu historial.",
            "¿Cómo recibir notificaciones de ofertas?" to "Activa las notificaciones en 'Configuración' y selecciona 'Ofertas y promociones'. Así recibirás alertas directamente en tu móvil.",
            "¿La app tiene modo oscuro?" to "Sí, ve a 'Configuración' y activa 'Modo oscuro' para una visualización más cómoda en la noche.",
            "¿Puedo usar la app sin internet?" to "Algunas funciones básicas estarán disponibles sin conexión, pero la mayoría de servicios requieren internet activo."
        )


        //Adapter
        adapter = FAQAdapter(faqList)
        recyclerView.adapter = adapter
    }

    //Volver
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    //Search
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.menu_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.queryHint = "Buscar pregunta..."

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrEmpty()) {
                    faqList.filter { it.first.contains(newText, ignoreCase = true) }
                } else faqList
                adapter.updateList(filtered)
                return true
            }
        })
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> { onBackPressed(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
