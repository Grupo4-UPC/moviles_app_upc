package com.upc.grupo4.atencionservicio

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.upc.grupo4.atencionservicio.model.ServiceModel
import com.upc.grupo4.atencionservicio.util.Constants

class EnterRequirementsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var btnTracking: MaterialButton
    private lateinit var btnInformation: MaterialButton
    private lateinit var tvLastInformationTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_enter_requirements)

        toolbar = findViewById(R.id.toolbar_enter_requirements)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        btnTracking = findViewById(R.id.btn_tracking_req)
        btnInformation = findViewById(R.id.btn_information_req)
        tvLastInformationTitle = findViewById(R.id.tv_last_information)

        val service: ServiceModel? =
            intent.getParcelableExtra(Constants.SERVICE)

        // Set initial state
        setSelectedButton(1, service)

        btnTracking.setOnClickListener {
            setSelectedButton(1, service)
        }

        btnInformation.setOnClickListener {
            setSelectedButton(2, service)
        }
    }

    private fun setSelectedButton(
        selected: Int,
        service: ServiceModel? = null
    ) {
        when (selected) {
            1 -> {
                styleButtonAsFilled(btnTracking)
                styleButtonAsOutlined(btnInformation)
                tvLastInformationTitle.visibility = FrameLayout.VISIBLE
                showEnterRequirements(service)
            }

            2 -> {
                styleButtonAsOutlined(btnTracking)
                styleButtonAsFilled(btnInformation)
                tvLastInformationTitle.visibility = FrameLayout.GONE
                showServiceInformation(service)
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

    private fun showEnterRequirements(service: ServiceModel?) {
        val enterRequirementsFragment = EnterRequirementsFragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.SERVICE, service)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.enter_requirements_container, enterRequirementsFragment)
            .commit()
    }

    private fun showServiceInformation(service: ServiceModel?) {
        val serviceInformationFragment = ServiceInformationFragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.SERVICE, service)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.enter_requirements_container, serviceInformationFragment)
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}