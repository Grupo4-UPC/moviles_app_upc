package com.upc.grupo4.atencionservicio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.upc.grupo4.atencionservicio.model.ServiceInformationModel
import com.upc.grupo4.atencionservicio.util.Constants

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
    private lateinit var tiPieces: TextInputEditText
    private lateinit var btnNextObservations: MaterialButton

    // Elements from step 4
    private lateinit var tvClientId: TextView
    private lateinit var tvClientName: TextView
    private lateinit var tvClientObservation: TextView
    private lateinit var signaturePad: SignaturePad
    private lateinit var btnFinish: MaterialButton

    private var serviceInformation: ServiceInformationModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceInformation = it.getParcelable(Constants.SERVICE_INFORMATION)
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
        tiPieces = view.findViewById(R.id.ti_pieces)
        btnNextObservations = view.findViewById(R.id.btn_next_observation)

        tvClientId = view.findViewById(R.id.tv_client_id)
        tvClientName = view.findViewById(R.id.tv_client_name)
        tvClientObservation = view.findViewById(R.id.tv_observations)
        signaturePad = view.findViewById(R.id.signature_pad)
        btnFinish = view.findViewById(R.id.btn_finish)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tiClientName.setText(serviceInformation?.clientName)
        tiClientName.requestFocus()

        tiClientId.setText(serviceInformation?.clientId)

        btnNextClientName.setOnClickListener {
            val clientName = tiClientName.text.toString()

            if (clientName != "") {
                serviceInformation?.clientName = clientName
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
                serviceInformation?.clientId = clientId
                layoutStep2.visibility = View.GONE

                tiObservations.setText(serviceInformation?.observations)
                tiPieces.setText(serviceInformation?.extraInformation)

                layoutStep3.visibility = View.VISIBLE
                tiObservations.requestFocus()
            } else {
                tiClientId.error = "El documento de identidad es requerido"
            }
        }

        btnNextObservations.setOnClickListener {
            val observations = tiObservations.text.toString()
            val pieces = tiPieces.text.toString()

            if (observations.isNotEmpty()) {
                serviceInformation?.observations = observations
            }

            if (pieces.isNotEmpty()) {
                serviceInformation?.extraInformation = pieces
            }

            tvClientId.text = serviceInformation?.clientId
            tvClientName.text = serviceInformation?.clientName
            tvClientObservation.text = serviceInformation?.observations

            layoutStep3.visibility = View.GONE
            layoutStep4.visibility = View.VISIBLE
        }



        signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                //Event triggered when the pad is touched
            }

            override fun onSigned() {
//                val signatureBitmap = signaturePad.getTransparentSignatureBitmap()
//                val outputStream: FileOutputStream =
//                    requireActivity().openFileOutput("signature.png", Activity.MODE_PRIVATE)
//                signatureBitmap.compress(
//                    android.graphics.Bitmap.CompressFormat.PNG,
//                    100,
//                    outputStream
//                )
//                outputStream.close()
//
//                val file = requireActivity().getFileStreamPath("signature.png")
//                serviceInformation?.signature = file.absolutePath

                serviceInformation?.isSigned = true
                btnFinish.isEnabled = true
            }

            override fun onClear() {
                serviceInformation?.isSigned = false
                btnFinish.isEnabled = false
            }
        })

        btnFinish.setOnClickListener {
            if (serviceInformation?.isSigned == true) {
                val resultIntent = Intent()
                // Put the ArrayList of Parcelable objects
                resultIntent.putExtra(
                    Constants.SERVICE_INFORMATION,
                    serviceInformation
                )
                requireActivity().setResult(Activity.RESULT_OK, resultIntent)
                requireActivity().finish()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(serviceInformation: ServiceInformationModel) =
            EnterRequirementsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.SERVICE_INFORMATION, serviceInformation)
                }
            }
    }
}