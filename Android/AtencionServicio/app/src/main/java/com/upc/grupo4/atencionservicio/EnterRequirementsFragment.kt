package com.upc.grupo4.atencionservicio

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.upc.grupo4.atencionservicio.model.ServiceModel
import com.upc.grupo4.atencionservicio.util.Constants
import java.io.File
import java.io.FileOutputStream

class EnterRequirementsFragment : Fragment() {

    private lateinit var layoutStep1: LinearLayout
    private lateinit var layoutStep2: LinearLayout
    private lateinit var layoutStep3: LinearLayout
    private lateinit var layoutStep4: LinearLayout

    // Elements from step 1
    private lateinit var tiClientName: TextInputEditText
    private lateinit var btnNextClientName: MaterialButton

    // Elements from step 2
    private lateinit var tiClientId: TextInputEditText
    private lateinit var btnNextClientId: MaterialButton

    // Elements from step 3
    private lateinit var tiObservations: TextInputEditText
    private lateinit var tiAdditionalInformation: TextInputEditText
    private lateinit var btnNextObservations: MaterialButton

    // Elements from step 4
    private lateinit var customerSummaryLayout: View
    private lateinit var tvClientId: TextView
    private lateinit var tvClientName: TextView
    private lateinit var tvClientObservation: TextView

    private lateinit var signaturePad: SignaturePad
    private lateinit var btnFinish: MaterialButton

    private var service: ServiceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            service = it.getParcelable(Constants.SERVICE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_enter_requirements, container, false)

        layoutStep1 = view.findViewById(R.id.layout_step_1)
        layoutStep2 = view.findViewById(R.id.layout_step_2)
        layoutStep3 = view.findViewById(R.id.layout_step_3)
        layoutStep4 = view.findViewById(R.id.layout_step_4)

        tiClientName = view.findViewById(R.id.ti_client_name)
        btnNextClientName = view.findViewById(R.id.btn_next_client_name)

        tiClientId = view.findViewById(R.id.ti_client_id)
        btnNextClientId = view.findViewById(R.id.btn_next_client_id)

        tiObservations = view.findViewById(R.id.ti_observations)
        tiAdditionalInformation = view.findViewById(R.id.ti_additional_info)
        btnNextObservations = view.findViewById(R.id.btn_next_observation)

        customerSummaryLayout = view.findViewById(R.id.customer_summary_layout)
        tvClientId = customerSummaryLayout.findViewById(R.id.tv_client_doc_id_value)
        tvClientName = customerSummaryLayout.findViewById(R.id.tv_client_name_value)
        tvClientObservation = customerSummaryLayout.findViewById(R.id.tv_observations_value)

        signaturePad = view.findViewById(R.id.signature_pad)
        btnFinish = view.findViewById(R.id.btn_finish)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tiClientName.setText(service?.clientName)
        tiClientName.requestFocus()

        tiClientId.setText(service?.clientDocId)

        btnNextClientName.setOnClickListener {
            val clientName = tiClientName.text.toString()

            if (clientName != "") {
                service?.serviceReceiverName = clientName
                layoutStep1.visibility = View.GONE
                layoutStep2.visibility = View.VISIBLE
                tiClientId.requestFocus()
            } else {
                tiClientName.error = "El nombre del cliente es requerido"
            }
        }

        btnNextClientId.setOnClickListener {
            val clientId = tiClientId.text.toString()

            if (clientId != "") {
                service?.serviceReceiverDocId = clientId
                layoutStep2.visibility = View.GONE

                layoutStep3.visibility = View.VISIBLE
                tiObservations.requestFocus()
            } else {
                tiClientId.error = "El documento de identidad es requerido"
            }
        }

        btnNextObservations.setOnClickListener {
            val observations = tiObservations.text.toString()
            val additionalInfo = tiAdditionalInformation.text.toString()

            if (observations.isNotEmpty()) {
                service?.newObservations = observations
            }

            if (additionalInfo.isNotEmpty()) {
                service?.additionalInformation = additionalInfo
            }

            tvClientId.text = service?.serviceReceiverDocId
            tvClientName.text = service?.serviceReceiverName
            tvClientObservation.text = service?.newObservations

            layoutStep3.visibility = View.GONE
            layoutStep4.visibility = View.VISIBLE
        }



        signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                //Event triggered when the pad is touched
            }

            override fun onSigned() {
                service?.isSigned = true
                btnFinish.isEnabled = true
            }

            override fun onClear() {
                service?.isSigned = false
                service?.signatureUrl = null
                btnFinish.isEnabled = false
            }
        })

        btnFinish.setOnClickListener {
            if (service?.isSigned == true) {
                val photoUri = getPhotoUri()
                service?.signatureUri = photoUri

                val resultIntent = Intent()
                resultIntent.putExtra(
                    Constants.SERVICE,
                    service
                )
                requireActivity().setResult(Activity.RESULT_OK, resultIntent)
                requireActivity().finish()
            }
        }
    }

    private fun getPhotoUri(): Uri {
        val imageDir = File(requireContext().filesDir, "Pictures/images")

        // Create the directories if they don't exist.
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }

        // Create the final file object.
        val signatureFile = File(imageDir, "signature.png")

        try {
            val signatureBitmap = signaturePad.getTransparentSignatureBitmap()
            val outputStream = FileOutputStream(signatureFile)

            signatureBitmap.compress(
                android.graphics.Bitmap.CompressFormat.PNG,
                100,
                outputStream
            )
            outputStream.flush()
            outputStream.close()

            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                signatureFile
            )

            return photoURI
        } catch (e: Exception) {
            // Handle potential errors (e.g., IOException)
            e.printStackTrace()

            return Uri.EMPTY
            // Toast.makeText(requireContext(), "Error al guardar la firma", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(service: ServiceModel) =
            EnterRequirementsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.SERVICE, service)
                }
            }
    }
}