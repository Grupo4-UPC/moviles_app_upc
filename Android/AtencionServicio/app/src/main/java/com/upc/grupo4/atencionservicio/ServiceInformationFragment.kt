package com.upc.grupo4.atencionservicio

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.upc.grupo4.atencionservicio.model.ServiceInformationModel

private const val ARG_SERVICE_INFORMATION = "service_information"
private const val GOOGLE_MAPS_URL = "http://maps.google.com/maps?q="

/**
 * A simple [Fragment] subclass.
 * Use the [ServiceInformationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ServiceInformationFragment : Fragment() {
    private var serviceInformation: ServiceInformationModel? = null

    private lateinit var txtClient: TextView
    private lateinit var txtCellphone: TextView
    private lateinit var txtAddress: TextView
    private lateinit var txtTypeService: TextView
    private lateinit var txtProduct: TextView
    private lateinit var txtDateService: TextView
    private lateinit var txtObservation: TextView
    private lateinit var txtServiceShift: TextView
    private lateinit var txtAddressReference: TextView
    private lateinit var btnCall: Button

    private var phoneNumberToCall: String? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action
                phoneNumberToCall?.let { makeCall(it) }
            } else {
                // Displays an alert dialog if the user denied the permission
                AlertDialog.Builder(requireContext())
                    .setTitle("Aviso")
                    .setMessage("Luego podrías cambiar el permiso en ajustes.")
                    .setPositiveButton("Ok", null)
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceInformation = it.getParcelable(ARG_SERVICE_INFORMATION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_service_information, container, false)

        txtClient = view.findViewById(R.id.txt_client)
        txtCellphone = view.findViewById(R.id.txt_cellphone)
        txtAddress = view.findViewById(R.id.txt_address)
        txtTypeService = view.findViewById(R.id.txt_type_service)
        txtProduct = view.findViewById(R.id.txt_product)
        txtDateService = view.findViewById(R.id.txt_date_service)
        txtObservation = view.findViewById(R.id.txt_observation)
        txtServiceShift = view.findViewById(R.id.txt_service_shift)
        txtAddressReference = view.findViewById(R.id.txt_address_reference)
        btnCall = view.findViewById(R.id.btn_Call)

        txtClient.text = serviceInformation?.clientName

        if (serviceInformation?.cellphone.isNullOrEmpty()) {
            btnCall.visibility = View.GONE
        }

        txtCellphone.text = serviceInformation?.cellphone
        txtAddress.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        txtAddress.text = serviceInformation?.address
        txtTypeService.text = serviceInformation?.typeService
        txtProduct.text = serviceInformation?.product
        txtDateService.text = serviceInformation?.dateService
        txtObservation.text = serviceInformation?.observation
        txtServiceShift.text = serviceInformation?.serviceShift
        txtAddressReference.text = serviceInformation?.addressReference

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnCall.setOnClickListener {
            phoneNumberToCall = txtCellphone.text.toString().trim()
            checkCallPermissionAndMakeCall(phoneNumberToCall!!)
        }

        if (!serviceInformation?.address.isNullOrEmpty()) {
            txtAddress.setOnClickListener {
                val map = GOOGLE_MAPS_URL + serviceInformation?.address
                val i = Intent(Intent.ACTION_VIEW, map.toUri())
                startActivity(i)
            }
        }
    }

    private fun checkCallPermissionAndMakeCall(phoneNumber: String) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                makeCall(phoneNumber)
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) -> {
                // If it does not have permission, ask for it
                showPermissionRationaleDialog(phoneNumber)
            }

            else -> {
                // Directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }
    }

    private fun makeCall(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        val phoneNumberToCall = "tel:$phoneNumber"
        callIntent.data = phoneNumberToCall.toUri()

        try {
            startActivity(callIntent)
        } catch (e: SecurityException) {
            Toast.makeText(
                requireContext(),
                "Error de seguridad al intentar llamar.",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "No se pudo iniciar la llamada.", Toast.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }
    }

    private fun showPermissionRationaleDialog(phoneNumber: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Permiso Necesario")
            .setMessage("Para realizar una llamada, la aplicación necesita acceso a la función de teléfono. Por favor, concede el permiso.")
            .setPositiveButton("Conceder") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param serviceInformation Parameter 1.
         * @return A new instance of fragment ServiceInformationFragment.
         */
        @JvmStatic
        fun newInstance(serviceInformation: ServiceInformationModel) =
            ServiceInformationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SERVICE_INFORMATION, serviceInformation.toString())
                }
            }
    }
}