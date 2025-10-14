package com.upc.grupo4.atencionservicio

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.upc.grupo4.atencionservicio.model.PhotoReference
import com.upc.grupo4.atencionservicio.model.ServiceModel
import com.upc.grupo4.atencionservicio.model.StatusModel
import com.upc.grupo4.atencionservicio.util.Constants


class StartServiceActivity : AppCompatActivity() {

    private lateinit var btnTracking: MaterialButton
    private lateinit var btnInformation: MaterialButton
    private lateinit var contentContainer: FrameLayout
    private lateinit var toolbar: Toolbar
    private var photoReferences: ArrayList<PhotoReference>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start_service)
        Log.d("StartServiceActivity", "StartServiceActivity iniciada correctamente")

        // Obtener los datos del Intent
        val service: ServiceModel? = intent.getParcelableExtra(Constants.SERVICE)
        photoReferences = intent.getParcelableArrayListExtra(Constants.PHOTO_REFERENCES)

        val statusList: ArrayList<StatusModel>? = intent.getParcelableArrayListExtra(Constants.STATUS_LIST)

        // Configuración de la barra de herramientas
        toolbar = findViewById(R.id.toolbar_start_service)

        // Cambiar el título del toolbar según el estado del servicio
        if (service != null && (service.status == "Realizado" || service.status == "No Realizado")) {
            toolbar.setTitle("Revisar Servicio")
        }

        // Log de las fotos para verificar qué fotos se están pasando
        photoReferences?.let {
            it.forEachIndexed { index, photoReference ->
                Log.d("StartServiceActivity", "Photo $index: ${photoReference.uri}")
            }
        }

        // Si hay fotos, pasarlas al fragmento
        if (photoReferences != null ) {
            Log.d("StartServiceActivity", "Todo ok fragmenttt")
            val fragment = ServiceTrackingViewFragment.newInstance(service)
            val bundle = Bundle()
            bundle.putParcelableArrayList(Constants.PHOTO_REFERENCES, photoReferences)
            Log.d("StartServiceActivity", "Passing photos: ${photoReferences?.size}")

            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.start_service_container, fragment)  // 
                .commit()

        } else {
            Log.d("StartServiceActivity", "No photo references found to pass to the fragment")
        }

        // Configuración del toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        // Configurar los botones
        btnTracking = findViewById(R.id.btn_tracking)
        btnInformation = findViewById(R.id.btn_information)
        contentContainer = findViewById(R.id.start_service_container)

        // Configurar el estado inicial del botón
        setSelectedButton(1, service, statusList)

        btnTracking.setOnClickListener {
            setSelectedButton(1, service, statusList)
        }

        btnInformation.setOnClickListener {
            setSelectedButton(2, service, null)
        }
    }

    private fun setSelectedButton(
        selected: Int,
        service: ServiceModel?,
        statusList: ArrayList<StatusModel>?
    ) {
        when (selected) {
            1 -> {
                styleButtonAsFilled(btnTracking)
                styleButtonAsOutlined(btnInformation)
                displayServiceTrackingFragment(service, statusList, photoReferences )
            }

            2 -> {
                styleButtonAsOutlined(btnTracking)
                styleButtonAsFilled(btnInformation)
                displayServiceInformationFragment(service)
            }
        }
    }

    private fun styleButtonAsFilled(button: MaterialButton) {
        button.strokeWidth = 0 // No stroke for filled
        button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue_500)
        button.setTextColor(ContextCompat.getColor(this, R.color.my_app_text_color_filled))
    }

    private fun styleButtonAsOutlined(button: MaterialButton) {
        button.strokeWidth = resources.getDimensionPixelSize(R.dimen.my_button_stroke_width)
        button.strokeColor = ContextCompat.getColorStateList(this, R.color.blue_500)
        button.backgroundTintList = ContextCompat.getColorStateList(
            this,
            R.color.transparent
        )
        button.setTextColor(ContextCompat.getColor(this, R.color.blue_500))
    }

    private fun displayServiceTrackingFragment(
        service: ServiceModel?,
        statusList: ArrayList<StatusModel>?,
        photoReferences: ArrayList<PhotoReference>?
    ) {
        Log.d("StartServiceActivity", "Estado del servicio: ${service?.status}")
        if (service?.status == "" || service?.status == null) {
            val serviceTrackingFragment = ServiceTrackingFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.SERVICE, service)
                    putParcelableArrayList(Constants.STATUS_LIST, statusList)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.start_service_container, serviceTrackingFragment)
                .commit()
        } else {
            val serviceTrackingViewFragment = ServiceTrackingViewFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.SERVICE, service)
                     putParcelableArrayList(Constants.PHOTO_REFERENCES, photoReferences)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.start_service_container, serviceTrackingViewFragment)
                .commit()
        }
    }

    private fun displayServiceInformationFragment(service: ServiceModel?) {
        val serviceInformationFragment = ServiceInformationFragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.SERVICE, service)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.start_service_container, serviceInformationFragment)
            .commit()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}