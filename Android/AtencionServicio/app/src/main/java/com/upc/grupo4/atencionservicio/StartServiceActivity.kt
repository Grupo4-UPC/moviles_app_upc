package com.upc.grupo4.atencionservicio

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.upc.grupo4.atencionservicio.model.ServiceInformationModel
import com.upc.grupo4.atencionservicio.model.ServiceModel
import com.upc.grupo4.atencionservicio.util.Constants


class StartServiceActivity : AppCompatActivity() {

    private lateinit var btnTracking: MaterialButton
    private lateinit var btnInformation: MaterialButton
    private lateinit var contentContainer: FrameLayout
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start_service)

        val service: ServiceModel? =
            intent.getParcelableExtra(Constants.SERVICE)

        val serviceInformation: ServiceInformationModel? =
            intent.getParcelableExtra(Constants.SERVICE_INFORMATION)

        toolbar = findViewById(R.id.toolbar_start_service)

        if (service != null && service.status == "Realizado") {
            toolbar.setTitle("Revisar Servicio")
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)


        btnTracking = findViewById(R.id.btn_tracking)
        btnInformation = findViewById(R.id.btn_information)
        contentContainer = findViewById(R.id.start_service_container)

        // Set initial state
        setSelectedButton(1, service, serviceInformation)

        btnTracking.setOnClickListener {
            setSelectedButton(1, service, serviceInformation)
        }

        btnInformation.setOnClickListener {
            setSelectedButton(2, null, serviceInformation)
        }
    }

    private fun setSelectedButton(
        selected: Int,
        service: ServiceModel?,
        serviceInformation: ServiceInformationModel? = null
    ) {
        when (selected) {
            1 -> {
                styleButtonAsFilled(btnTracking)
                styleButtonAsOutlined(btnInformation)
                showServiceTracking(service, serviceInformation)
            }

            2 -> {
                styleButtonAsOutlined(btnTracking)
                styleButtonAsFilled(btnInformation)
                showServiceInformation(serviceInformation)
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

    private fun showServiceTracking(
        service: ServiceModel?,
        serviceInformation: ServiceInformationModel?
    ) {
        val serviceTrackingFragment = ServiceTrackingFragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.SERVICE, service)
                putParcelable(Constants.SERVICE_INFORMATION, serviceInformation)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.start_service_container, serviceTrackingFragment)
            .commit()
    }

    private fun showServiceInformation(serviceInformation: ServiceInformationModel?) {
        val serviceInformationFragment = ServiceInformationFragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.SERVICE_INFORMATION, serviceInformation)
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