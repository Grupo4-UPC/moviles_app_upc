package com.upc.grupo4.atencionservicio

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.upc.grupo4.atencionservicio.model.ServiceInformationModel

class ServiceInformationActivity : AppCompatActivity() {
    lateinit var serviceInformationContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_service_information)

        serviceInformationContainer = findViewById(R.id.service_info_container)

        val serviceInformation: ServiceInformationModel? =
            intent.getParcelableExtra("service_information")


        val finishedServicesFragment = ServiceInformationFragment().apply {
            arguments = Bundle().apply {
                putParcelable("service_information", serviceInformation)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.service_info_container, finishedServicesFragment)
            .commit()
    }
}