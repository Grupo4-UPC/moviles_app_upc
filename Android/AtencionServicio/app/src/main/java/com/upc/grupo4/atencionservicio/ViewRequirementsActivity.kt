package com.upc.grupo4.atencionservicio

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import coil.load
import com.upc.grupo4.atencionservicio.model.ServiceModel
import com.upc.grupo4.atencionservicio.model.SignatureClient
import com.upc.grupo4.atencionservicio.util.Constants

class ViewRequirementsActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var customerSummaryReviewLayout: View
    private lateinit var tvClientDocId: TextView
    private lateinit var tvClientName: TextView
    private lateinit var tvClientObservation: TextView
    private lateinit var tvAdditionalInfoLabel: TextView
    private lateinit var tvAdditionalInfoValue: TextView
    private lateinit var tvSignLabel: TextView
    private lateinit var tvSignValue: TextView
    private lateinit var signPhotoView: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_requirements)

        val service: ServiceModel? =
            intent.getParcelableExtra(Constants.SERVICE)
        val signatureList: ArrayList<SignatureClient>? =
            intent.getParcelableArrayListExtra(Constants.SIGNATURE_CLIENT)

        toolbar = findViewById(R.id.toolbar_review_requirements)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        customerSummaryReviewLayout = findViewById(R.id.customer_summary_review_layout)
        tvClientDocId = customerSummaryReviewLayout.findViewById(R.id.tv_client_doc_id_value)
        tvClientName = customerSummaryReviewLayout.findViewById(R.id.tv_client_name_value)
        tvClientObservation = customerSummaryReviewLayout.findViewById(R.id.tv_observations_value)
        tvAdditionalInfoLabel =
            customerSummaryReviewLayout.findViewById(R.id.tv_additional_info_label)
        tvAdditionalInfoValue =
            customerSummaryReviewLayout.findViewById(R.id.tv_additional_info_value)
        tvSignLabel = customerSummaryReviewLayout.findViewById(R.id.tv_sign_label)
        tvSignValue = customerSummaryReviewLayout.findViewById(R.id.tv_sign_value)
        signPhotoView = findViewById(R.id.photo_sign_view)

        tvAdditionalInfoLabel.visibility = View.VISIBLE
        tvAdditionalInfoValue.visibility = View.VISIBLE
        tvSignLabel.visibility = View.VISIBLE
        tvSignValue.visibility = View.VISIBLE

        tvClientDocId.text = service?.serviceReceiverDocId
        tvClientName.text = service?.serviceReceiverName
        tvClientObservation.text = service?.newObservations
        tvAdditionalInfoValue.text = service?.additionalInformation



        if (!signatureList.isNullOrEmpty()) {
            val signature = signatureList[0] // Mostrar la primera firma

            signature.uri?.let { uri ->
                displayPhoto(uri)
            } ?: run {
                Log.e("ViewRequirementsActivity", "La firma no contiene un URI válido")
                val ivPhotoPreview: ImageView = signPhotoView.findViewById(R.id.iv_sign_photo_view)
               // ivPhotoPreview.setImageResource(R.drawable.ic_no_signature)
            }
        } else {
            Log.e("ViewRequirementsActivity", "No hay firmas recibidas")
        }
    }

    private fun displayPhoto(uri: Uri) {
        val ivPhotoPreview: ImageView = signPhotoView.findViewById(R.id.iv_sign_photo_view)

        ivPhotoPreview.load(uri) {
            size(300, 300)
            crossfade(true)
            placeholder(R.drawable.ic_placeholder_image)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}