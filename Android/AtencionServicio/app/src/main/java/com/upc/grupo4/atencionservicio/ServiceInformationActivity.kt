package com.upc.grupo4.atencionservicio

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.upc.grupo4.atencionservicio.model.ServiceInformationModel
import com.upc.grupo4.atencionservicio.util.Constants

class ServiceInformationActivity : AppCompatActivity() {
    lateinit var serviceInformationContainer: FrameLayout
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_service_information)

        toolbar = findViewById(R.id.toolbar_service_info)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        serviceInformationContainer = findViewById(R.id.service_info_container)

        val serviceInformation: ServiceInformationModel? =
            intent.getParcelableExtra(Constants.SERVICE_INFORMATION)


        val serviceInformationFragment = ServiceInformationFragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.SERVICE_INFORMATION, serviceInformation)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.service_info_container, serviceInformationFragment)
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}