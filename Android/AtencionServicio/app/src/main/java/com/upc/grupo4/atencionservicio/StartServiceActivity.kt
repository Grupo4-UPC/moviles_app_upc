package com.upc.grupo4.atencionservicio

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class StartServiceActivity : AppCompatActivity() {

    private lateinit var trackingBtn: MaterialButton
    private lateinit var informationBtn: MaterialButton
    private lateinit var contentContainer: FrameLayout

    // Information view variables
    private lateinit var txtClient: TextView
    private lateinit var txtCellphone: TextView
    private lateinit var txtAddress: TextView
    private lateinit var txtTypeService: TextView
    private lateinit var txtProduct: TextView
    private lateinit var txtDateService: TextView
    private lateinit var txtObservation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start_service)

        trackingBtn = findViewById(R.id.tracking_btn)
        informationBtn = findViewById(R.id.information_btn)
        contentContainer = findViewById(R.id.contentContainer)

        // Set initial state
        setSelectedButton(1)

        trackingBtn.setOnClickListener {
            setSelectedButton(1)
        }

        informationBtn.setOnClickListener {
            setSelectedButton(2)
        }
    }

    private fun setSelectedButton(selected: Int) {
        when (selected) {
            1 -> {
                styleButtonAsFilled(trackingBtn)
                styleButtonAsOutlined(informationBtn)
                showContent(R.layout.tracking_content)
            }

            2 -> {
                styleButtonAsOutlined(trackingBtn)
                styleButtonAsFilled(informationBtn)
                showContent(R.layout.information_content)
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

    private fun showContent(layoutId: Int) {
        contentContainer.removeAllViews()
        val view = layoutInflater.inflate(layoutId, contentContainer, false)

        if (layoutId == R.layout.information_content) {
            txtClient = view.findViewById(R.id.txt_client)
            txtCellphone = view.findViewById(R.id.txt_cellphone)
            txtAddress = view.findViewById(R.id.txt_address)
            txtTypeService = view.findViewById(R.id.txt_type_service)
            txtProduct = view.findViewById(R.id.txt_product)
            txtDateService = view.findViewById(R.id.txt_date_service)
            txtObservation = view.findViewById(R.id.txt_observation)

            txtClient.text = "Jose Lopez Perez"
            txtCellphone.text = "943562152"
            txtAddress.text = "Jr Los Sauces 123 - Urb. Sagitario - Surco"
            txtTypeService.text = "Visita tecnica cabinas / duchas / tinas"
            txtProduct.text = "10787 - Columna asturias"
            txtDateService.text = "2025-08-01"
            txtObservation.text = "Ninguno"
        }

        contentContainer.addView(view)
    }
}