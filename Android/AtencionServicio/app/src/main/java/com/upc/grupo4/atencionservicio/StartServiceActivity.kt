package com.upc.grupo4.atencionservicio

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.upc.grupo4.atencionservicio.model.ServiceModel
import com.upc.grupo4.atencionservicio.model.StatusModel
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

        val statusList: ArrayList<StatusModel>? =
            intent.getParcelableArrayListExtra(Constants.STATUS_LIST)

        toolbar = findViewById(R.id.toolbar_start_service)

        if (service != null && (service.status == "Realizado" || service.status == "No Realizado")) {
            toolbar.setTitle("Revisar Servicio")
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)


        btnTracking = findViewById(R.id.btn_tracking)
        btnInformation = findViewById(R.id.btn_information)
        contentContainer = findViewById(R.id.start_service_container)

        // Set initial state
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
                displayServiceTrackingFragment(service, statusList)
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
        statusList: ArrayList<StatusModel>?
    ) {
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