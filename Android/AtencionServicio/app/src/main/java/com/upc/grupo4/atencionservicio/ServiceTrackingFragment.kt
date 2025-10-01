package com.upc.grupo4.atencionservicio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.upc.grupo4.atencionservicio.dialogs.InfoDialogFragment
import com.upc.grupo4.atencionservicio.model.PhotoReference
import com.upc.grupo4.atencionservicio.model.ServiceInformationModel
import com.upc.grupo4.atencionservicio.util.Constants

/**
 * A simple [Fragment] subclass.
 * Use the [ServiceTrackingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ServiceTrackingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var orderId: String? = null
    private var serviceInformation: ServiceInformationModel? = null
    private lateinit var spStatus: Spinner
    private lateinit var spSubStatus: Spinner
    private lateinit var btnTakePictures: MaterialButton
    private lateinit var btnEnterRequirements: MaterialButton
    private lateinit var btnFinishService: MaterialButton
    private lateinit var ivServiceIcon: ImageView
    private lateinit var ivPhotoIcon: ImageView
    private lateinit var ivRegisterIcon: ImageView
    private lateinit var registerPhotosLauncher: ActivityResultLauncher<Intent>
    private lateinit var registerRequirementsLauncher: ActivityResultLauncher<Intent>
    private var receivedPhotoReferences: List<PhotoReference>? = null
    private var statusValue: Int? = 0
    private var statusValueStr: String? = ""
    private var subStatusValueStr: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            orderId = it.getString(Constants.ORDER_ID)
            serviceInformation = it.getParcelable(Constants.SERVICE_INFORMATION)
        }

        registerPhotosLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("ServiceTrackingFragment", "Photo registration was successful.")
                val data: Intent? = result.data
                // Get the ArrayList of Parcelable objects
                val photoRefs = data?.getParcelableArrayListExtra<PhotoReference>(
                    Constants.PHOTO_REFERENCES
                )
                if (photoRefs != null && photoRefs.isNotEmpty()) {
                    receivedPhotoReferences = photoRefs
                    updatePhotoIconColor(R.color.blue_400) // this can be changed to a different color
                    updateTakePhotoButtonColor(R.color.blue_400)

//                    Log.d("ServiceTrackingFragment", "Received ${photoRefs.size} photo references:")
//                    photoRefs.forEach { ref ->
//                        Log.d(
//                            "ServiceTrackingFragment",
//                            "Type: ${ref.type}, URI: ${ref.uri}, Path: ${ref.filePath}"
//                        )
//                    }

                    Toast.makeText(
                        requireContext(),
                        "${photoRefs.size} fotos recibidas",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Log.d(
                        "ServiceTrackingFragment",
                        "No photo references received or list is empty."
                    )
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                Log.d("ServiceTrackingFragment", "Photo registration was cancelled.")
                // Handle cancellation if needed
            }
        }

        registerRequirementsLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("ServiceTrackingFragment", "Requirements registration was successful.")
                val data: Intent? = result.data

                val returnedInfo = data?.getParcelableExtra<ServiceInformationModel>(
                    Constants.SERVICE_INFORMATION // Key used by EnterRequirementsFragment
                )
                if (returnedInfo != null) {
                    serviceInformation = returnedInfo
                    updateRegisterIconColor(R.color.blue_400)
                    updateRegisterButtonColor(R.color.blue_400)
                    btnFinishService.isEnabled = true
                } else {
                    Log.d("ServiceTrackingFragment", "No service information returned.")
                }

            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                Log.d("ServiceTrackingFragment", "Requirements registration was cancelled.")
                // Handle cancellation if needed
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service_tracking, container, false)

        spStatus = view.findViewById(R.id.sp_status)
        spSubStatus = view.findViewById(R.id.sp_sub_status)
        btnTakePictures = view.findViewById(R.id.btn_take_pictures)
        btnEnterRequirements = view.findViewById(R.id.btn_enter_requirements)
        btnFinishService = view.findViewById(R.id.btn_finish_service)
        ivServiceIcon = view.findViewById(R.id.iv_service_icon)
        ivPhotoIcon = view.findViewById(R.id.iv_photo_icon)
        ivRegisterIcon = view.findViewById(R.id.iv_register_icon)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupStatusSpinner()
        loadSubStatusSpinner()

        btnTakePictures.setOnClickListener {
            launchRegisterPhotosActivity()
        }

        btnEnterRequirements.setOnClickListener {
            launchEnterRequirementsActivity()
        }
    }

    private fun setupStatusSpinner() {
        val actualStatusOptions = resources.getStringArray(R.array.status_options)

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_custom, // Setting custom spinner item layout
            actualStatusOptions
        )

        // Setting custom dropdown layout
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)

        // 3. Apply the adapter to the spinner
        spStatus.adapter = adapter

        spStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedStatus = parent?.getItemAtPosition(position).toString()

                val selectedTextView = view as? TextView

                when (position) {
                    0 -> {
                        updateSpinnerWithDefaultStyles(selectedTextView)
                        statusValue = 0
                        statusValueStr = ""
                        loadSubStatusSpinner()
                    }

                    else -> {
                        updateSpinnerWithSelectedStyles(selectedTextView)
                        statusValue = position
                        statusValueStr = selectedStatus
                        loadSubStatusSpinner()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Another interface callback
            }
        }

        // Set spinner to show the hint initially
        spStatus.setSelection(0, false)
    }


    private fun loadSubStatusSpinner() {
        val subStatusOptionsResource = when (statusValue) {
            1 -> R.array.sub_status_for_done
            2 -> R.array.sub_status_for_not_done
            else -> R.array.sub_status_initial
        }

        val subStatusOptions = resources.getStringArray(subStatusOptionsResource)

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_custom, // Setting custom spinner item layout
            subStatusOptions
        )

        // Setting custom dropdown layout
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)

        spSubStatus.adapter = adapter

        spSubStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedSubStatus = parent?.getItemAtPosition(position).toString()

                val selectedTextView = view as? TextView

                when (position) {
                    0 -> {
                        updateSpinnerWithDefaultStyles(selectedTextView)
                        subStatusValueStr = ""
                        updateServiceIconColor(R.color.button_disabled_background_grey)
                    }

                    else -> {
                        updateSpinnerWithSelectedStyles(selectedTextView)
                        subStatusValueStr = selectedSubStatus
                        updateServiceIconColor(R.color.blue_400)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Another interface callback
            }
        }
    }

    private fun launchRegisterPhotosActivity() {
        if (statusValueStr != "" && subStatusValueStr != "") {
            val intent = Intent(requireContext(), RegisterPhotosActivity::class.java)
            intent.putExtra(Constants.STATUS, statusValueStr)
            intent.putExtra(Constants.SUB_STATUS, subStatusValueStr)
            registerPhotosLauncher.launch(intent)
        } else {
            val dialogMessage =
                "Para poder continuar con la captura de fotos debes seleccionar un estado y subestado."
            InfoDialogFragment.newInstance(
                message = dialogMessage,
            ).show(parentFragmentManager, "InfoDialogFragmentTag")
        }
    }

    private fun launchEnterRequirementsActivity() {
//        if (statusValueStr != "" && subStatusValueStr != "" && receivedPhotoReferences != null) {
        val intent = Intent(requireContext(), EnterRequirementsActivity::class.java)
        intent.putExtra(Constants.SERVICE_INFORMATION, serviceInformation)
        registerRequirementsLauncher.launch(intent)
//        } else {
//            val dialogMessage =
//                "Para poder continuar con el registro, debes completar los pasos previos."
//            InfoDialogFragment.newInstance(
//                message = dialogMessage,
//            ).show(parentFragmentManager, "InfoDialogFragmentTag")
//        }
    }


    private fun updateSpinnerWithDefaultStyles(selectedTextView: TextView?) {
        selectedTextView?.setBackgroundResource(R.drawable.spinner_custom_background)
        selectedTextView?.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.spinner_hint_text_color
            )
        )
    }

    private fun updateSpinnerWithSelectedStyles(selectedTextView: TextView?) {
        selectedTextView?.setBackgroundResource(R.drawable.spinner_background_blue)
        selectedTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private fun updateServiceIconColor(colorResId: Int) {
        val color = ContextCompat.getColor(requireContext(), colorResId)
        ivServiceIcon.setColorFilter(color)
    }

    private fun updatePhotoIconColor(colorResId: Int) {
        val color = ContextCompat.getColor(requireContext(), colorResId)
        ivPhotoIcon.setColorFilter(color)
    }

    private fun updateTakePhotoButtonColor(colorResId: Int) {
        val color = ContextCompat.getColor(requireContext(), colorResId)
        btnTakePictures.setBackgroundColor(color)
    }

    private fun updateRegisterIconColor(colorResId: Int) {
        val color = ContextCompat.getColor(requireContext(), colorResId)
        ivRegisterIcon.setColorFilter(color)
    }

    private fun updateRegisterButtonColor(colorResId: Int) {
        val color = ContextCompat.getColor(requireContext(), colorResId)
        btnEnterRequirements.setBackgroundColor(color)
    }

    companion object {
        @JvmStatic
        fun newInstance(orderId: String) =
            ServiceTrackingFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.ORDER_ID, orderId)
                }
            }
    }
}